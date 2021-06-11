package com.boswelja.watchconnection.tizen

import android.content.Context
import com.boswelja.watchconnection.core.Message
import com.boswelja.watchconnection.core.Status
import com.boswelja.watchconnection.core.Watch
import com.boswelja.watchconnection.core.WatchPlatform
import com.samsung.android.sdk.accessory.SAAgentV2
import java.util.UUID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * A [WatchPlatform] with support for Tizen via Samsung's Accessory SDK.
 * @param context See [Context].
 */
class TizenPlatform(
    context: Context
) : WatchPlatform {

    private val coroutineScope = MainScope()

    private val incomingMessagesFlow = MutableSharedFlow<Message>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val messageReceiver = object : MessageReceiver() {
        override fun onMessageReceived(watchId: UUID, message: String, data: ByteArray?) {
            incomingMessagesFlow.tryEmit(
                Message(
                    watchId,
                    message,
                    data
                )
            )
        }
    }

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

    override val incomingMessages: Flow<Message> = incomingMessagesFlow

    init {
        coroutineScope.launch {
            incomingMessagesFlow.subscriptionCount.collect { subscriberCount ->
                if (subscriberCount > 0) {
                    accessoryAgent.registerMessageListener(messageReceiver)
                } else {
                    accessoryAgent.registerMessageListener(messageReceiver)
                }
            }
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
    override suspend fun sendMessage(watchId: String, message: String, data: ByteArray?): Boolean =
        accessoryAgent.sendMessage(watchId, message, data)

    companion object {
        const val CAPABILITY_MESSAGE = "/request_capabilities"
        const val PLATFORM = "TIZEN"
    }
}
