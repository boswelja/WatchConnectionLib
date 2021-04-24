package com.boswelja.watchconnection.tizen

import android.content.Context
import com.boswelja.watchconnection.core.MessageListener
import com.boswelja.watchconnection.core.Watch
import com.boswelja.watchconnection.core.WatchPlatform
import com.samsung.android.sdk.accessory.SAAgentV2
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

/**
 * A [WatchPlatform] with support for Tizen via Samsung's Accessory SDK.
 * @param context See [Context].
 */
class TizenPlatform(
    context: Context
) : WatchPlatform {

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
    override fun allWatches(): Flow<Watch> = accessoryAgent.allWatches()

    @ExperimentalCoroutinesApi
    override fun watchesWithApp(): Flow<Watch> = accessoryAgent.allWatches()

    @ExperimentalCoroutinesApi
    override fun getCapabilitiesFor(watchId: String): Flow<String> =
        accessoryAgent.getCapabilitiesFor(watchId)

    @Suppress("BlockingMethodInNonBlockingContext")
    @ExperimentalCoroutinesApi
    override suspend fun sendMessage(watchId: String, message: String, data: ByteArray?): Boolean =
        accessoryAgent.sendMessage(watchId, message, data)

    override fun addMessageListener(listener: MessageListener) =
        accessoryAgent.registerMessageListener(listener)

    override fun removeMessageListener(listener: MessageListener) =
        accessoryAgent.unregisterMessageListener(listener)

    companion object {
        const val CAPABILITY_MESSAGE = "/request_capabilities"
        const val PLATFORM = "TIZEN"
    }
}
