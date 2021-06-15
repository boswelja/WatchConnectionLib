package com.boswelja.watchconnection.tizen

import android.content.Context
import com.boswelja.watchconnection.core.Platform
import com.boswelja.watchconnection.core.Watch
import com.boswelja.watchconnection.core.discovery.Status
import com.boswelja.watchconnection.core.message.Message
import com.samsung.android.sdk.accessory.SAAgentV2
import java.util.UUID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

/**
 * A [Platform] with support for Tizen via Samsung's Accessory SDK.
 * @param context See [Context].
 */
class TizenPlatform(
    context: Context
) : Platform {

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

    override val platformIdentifier = PLATFORM

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

    @ExperimentalCoroutinesApi
    override fun allWatches(): Flow<List<Watch>> = accessoryAgent.allWatches()

    @ExperimentalCoroutinesApi
    override fun watchesWithApp(): Flow<List<Watch>> = accessoryAgent.allWatches()

    @ExperimentalCoroutinesApi
    override fun getCapabilitiesFor(watchId: String): Flow<List<String>> =
        accessoryAgent.getCapabilitiesFor(watchId)

    @ExperimentalCoroutinesApi
    override fun getStatusFor(watchId: String): Flow<Status> = allWatches().map { allWatches ->
        if (allWatches.any { it.platformId == watchId }) Status.CONNECTED
        else Status.DISCONNECTED
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    @ExperimentalCoroutinesApi
    override suspend fun sendMessage(
        watchId: String,
        message: String,
        data: ByteArray?,
        priority: Message.Priority
    ): Boolean = accessoryAgent.sendMessage(watchId, message, data)

    companion object {
        const val CAPABILITY_MESSAGE = "/request_capabilities"
        const val PLATFORM = "TIZEN"
    }
}
