package com.boswelja.watchconnection.tizen

import android.content.Context
import com.boswelja.watchconnection.core.message.Message
import com.boswelja.watchconnection.core.message.MessagePlatform
import com.boswelja.watchconnection.tizen.Constants.TIZEN_PLATFORM
import com.samsung.android.sdk.accessory.SAAgentV2
import java.util.UUID
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
    override fun incomingMessages(): Flow<Message> = callbackFlow {
        val receiver = object : MessageReceiver() {
            override fun onMessageReceived(watchId: UUID, message: String, data: ByteArray?) {
                val messageData = Message(
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
        priority: Message.Priority
    ): Boolean = accessoryAgent.sendMessage(watchId, message, data)
}