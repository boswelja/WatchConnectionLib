package com.boswelja.watchconnection.tizen

import android.content.Context
import com.boswelja.watchconnection.common.message.Messages.sendBroadcast
import com.boswelja.watchconnection.core.Watch
import com.boswelja.watchconnection.tizen.Constants.CAPABILITY_MESSAGE
import com.boswelja.watchconnection.tizen.Constants.TIZEN_PLATFORM
import com.samsung.android.sdk.SsdkUnsupportedException
import com.samsung.android.sdk.accessory.SA
import com.samsung.android.sdk.accessory.SAAgentV2
import com.samsung.android.sdk.accessory.SAMessage
import com.samsung.android.sdk.accessory.SAPeerAgent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class TizenAccessoryAgent internal constructor(
    context: Context,
    accessory: SA
) : SAAgentV2(TAG, context) {

    private val capabilityJob = Job()
    private val findPeerJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val allWatches = MutableStateFlow<List<Watch>>(emptyList())

    private val peerMap = HashMap<String, SAPeerAgent>()
    private val messageListeners = mutableListOf<MessageReceiver>()

    // Keep a map of channels to message IDs to
    private val messageChannelMap = HashMap<Int, Channel<Boolean>>()

    // Keep a map of peer agents to their capabilities
    private val capabilityMap = HashMap<String, Flow<List<String>>>()

    private val saMessage = object : SAMessage(this) {
        override fun onReceive(peerAgent: SAPeerAgent?, data: ByteArray?) {
            if (peerAgent == null) return
            data?.let {
                val (message, messageData) = Messages.fromByteArray(data)
                if (message == CAPABILITY_MESSAGE) {
                    // Capability message, assume we have data
                    handleCapabilityMessage(peerAgent, messageData!!)
                } else {
                    val watchId = Watch.createUUID(TIZEN_PLATFORM, peerAgent.accessory.accessoryId)
                    messageListeners.forEach {
                        it.onMessageReceived(watchId, message, data)
                    }
                    sendBroadcast(applicationContext, watchId, message, data)
                }
            }
        }

        override fun onSent(peerAgent: SAPeerAgent?, id: Int) {
            messageChannelMap[id]?.trySend(true)
        }

        override fun onError(peerAgent: SAPeerAgent?, id: Int, errorCode: Int) {
            messageChannelMap[id]?.trySend(false)
        }
    }

    init {
        try {
            accessory.initialize(context)
            accessory.isFeatureEnabled(SA.SERVICE_MESSAGE)
        } catch (e: SsdkUnsupportedException) {
            // TODO SDK not supported, handle reason
            releaseAgent()
        } catch (e: Exception) {
            // TODO We can't use the Accessory SDK for some reason, handle it
            releaseAgent()
        }
    }

    @ExperimentalCoroutinesApi
    fun allWatches(): Flow<List<Watch>> {
        findPeerJob.cancel()
        findPeerAgents()
        return allWatches
    }

    @ExperimentalCoroutinesApi
    fun getCapabilitiesFor(watchId: String): Flow<List<String>> {
        var flow = capabilityMap[watchId]
        if (flow == null) {
            // Flow doesn't already exist, create a new one
            flow = MutableStateFlow(emptyList())
            capabilityMap[watchId] = flow
        } else if (flow is MutableStateFlow) {
            // Reset replay cache if possible
            flow.resetReplayCache()
        }

        // Request capability stream
        coroutineScope.launch(Dispatchers.IO + capabilityJob) {
            sendMessage(watchId, CAPABILITY_MESSAGE, null)
        }

        return flow
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    @ExperimentalCoroutinesApi
    suspend fun sendMessage(watchId: String, message: String, data: ByteArray?): Boolean {
        val targetAgent = peerMap[watchId] ?: return false
        val bytes = Messages.toByteArray(message, data)

        // Create channel and map message ID to it
        val channel = Channel<Boolean>(capacity = 1)
        val id = saMessage.secureSend(targetAgent, bytes)
        messageChannelMap[id] = channel

        // If channel has sent true, return success
        return withTimeoutOrNull(OPERATION_TIMEOUT) { channel.receive() } == true
    }

    fun registerMessageListener(listener: MessageReceiver) {
        messageListeners.add(listener)
    }

    fun unregisterMessageListener(listener: MessageReceiver) {
        messageListeners.remove(listener)
    }

    override fun onFindPeerAgentsResponse(peers: Array<out SAPeerAgent>?, result: Int) {
        coroutineScope.launch(Dispatchers.Default + findPeerJob) {
            allWatches.emit(
                peers?.map { peerAgent ->
                    Watch(
                        peerAgent.accessory.name,
                        peerAgent.accessory.accessoryId,
                        TIZEN_PLATFORM
                    )
                } ?: emptyList()
            )
        }
    }

    /**
     * Handle a [Constants.CAPABILITY_MESSAGE].
     * @param peer The [SAPeerAgent] that sent the capability data.
     * @param data The received capability data.
     */
    private fun handleCapabilityMessage(peer: SAPeerAgent, data: ByteArray) {
        coroutineScope.launch(Dispatchers.IO + capabilityJob) {
            capabilityMap[peer.accessory.accessoryId]?.let { flow ->
                if (flow is MutableStateFlow) {
                    flow.emit(String(data, Charsets.UTF_8).split('|'))
                }
            }
        }
    }

    companion object {
        private const val TAG = "TizenConnectionHandler"
        private const val OPERATION_TIMEOUT = 1000L
    }
}
