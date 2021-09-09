package com.boswelja.watchconnection.tizen

import android.content.Context
import com.boswelja.watchconnection.common.message.MessagePriority
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.boswelja.watchconnection.core.message.MessagePlatform
import com.boswelja.watchconnection.tizen.Constants.TIZEN_PLATFORM
import com.samsung.android.sdk.accessory.SAAgentV2
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class TizenMessagePlatform(context: Context) : MessagePlatform {

    override val platformIdentifier: String = TIZEN_PLATFORM

    private lateinit var accessoryAgent: TizenAccessoryAgent
    var isReady: Boolean = false
        private set

    init {
        SAAgentV2.requestAgent(
            context,
            TizenAccessoryAgent::class.java.name,
            object : SAAgentV2.RequestAgentCallback {
                override fun onAgentAvailable(agent: SAAgentV2?) {
                    if (agent is TizenAccessoryAgent) {
                        accessoryAgent = agent
                        isReady = true
                    } else {
                        throw Exception("Agent provided was not our agent")
                    }
                }

                override fun onError(errorCode: Int, message: String?) {
                    throw Exception(message)
                }
            }
        )
    }

    @ExperimentalCoroutinesApi
    override fun incomingMessages(): Flow<ReceivedMessage<ByteArray?>> = callbackFlow {
        val receiver = object : MessageReceiver() {
            override fun onMessageReceived(watchId: String, message: String, data: ByteArray?) {
                val messageData = ReceivedMessage(
                    watchId, message, data
                )
                trySend(
                    messageData
                )
            }
        }

        accessoryAgent.registerMessageListener(receiver)

        awaitClose {
            accessoryAgent.unregisterMessageListener(receiver)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    @ExperimentalCoroutinesApi
    override suspend fun sendMessage(
        watchId: String,
        message: String,
        data: ByteArray?,
        priority: MessagePriority
    ): Boolean = accessoryAgent.sendMessage(watchId, message, data)
}
