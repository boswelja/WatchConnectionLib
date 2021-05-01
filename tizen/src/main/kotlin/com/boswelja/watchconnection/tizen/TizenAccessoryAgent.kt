package com.boswelja.watchconnection.tizen

import android.content.Context
import com.boswelja.watchconnection.core.MessageListener
import com.boswelja.watchconnection.core.Messages.sendBroadcast
import com.boswelja.watchconnection.core.Watch
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
import kotlinx.coroutines.channels.receiveOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class TizenAccessoryAgent internal constructor(
    context: Context,
    accessory: SA
) : SAAgentV2(TAG, context) {

    private val capabilityJob = Job()
    private val findPeerJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val allWatches = MutableStateFlow<Array<Watch>?>(null)

    private val peerMap = HashMap<String, SAPeerAgent>()
    private val messageListeners = ArrayList<MessageListener>()

    // Keep a map of channels to message IDs to
    private val messageChannelMap = HashMap<Int, Channel<Boolean>>()

    // Keep a map of peer agents to their capabilities
    private val capabilityMap = HashMap<String, Flow<Array<String>?>>()

    private val saMessage = object : SAMessage(this) {
        override fun onReceive(peerAgent: SAPeerAgent?, data: ByteArray?) {
            if (peerAgent == null) return
            data?.let {
                val (message, messageData) = Messages.fromByteArray(data)
                if (message == TizenPlatform.CAPABILITY_MESSAGE) {
                    // Capability message, assume we have data
                    handleCapabilityMessage(peerAgent, messageData!!)
                } else {
                    handleMessage(peerAgent, message, messageData)
                }
            }
        }

        override fun onSent(peerAgent: SAPeerAgent?, id: Int) {
            messageChannelMap[id]?.offer(true)
        }

        override fun onError(peerAgent: SAPeerAgent?, id: Int, errorCode: Int) {
            messageChannelMap[id]?.offer(false)
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
    fun allWatches(): Flow<Array<Watch>> {
        findPeerJob.cancel()
        findPeerAgents()
        return allWatches.mapNotNull { it }
    }

    @ExperimentalCoroutinesApi
    fun getCapabilitiesFor(watchId: String): Flow<Array<String>> {
        var flow = capabilityMap[watchId]
        if (flow == null) {
            // Flow doesn't already exist, create a new one
            flow = MutableStateFlow(null)
            capabilityMap[watchId] = flow
        } else if (flow is MutableStateFlow) {
            // Reset replay cache if possible
            flow.resetReplayCache()
        }

        // Request capability stream
        coroutineScope.launch(Dispatchers.IO + capabilityJob) {
            sendMessage(watchId, TizenPlatform.CAPABILITY_MESSAGE, null)
        }

        return flow.mapNotNull { it }
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
        return withTimeoutOrNull(OPERATION_TIMEOUT) { channel.receiveOrNull() } == true
    }

    fun registerMessageListener(listener: MessageListener) {
        messageListeners.add(listener)
    }

    fun unregisterMessageListener(listener: MessageListener) {
        messageListeners.remove(listener)
    }

    override fun onFindPeerAgentsResponse(peers: Array<out SAPeerAgent>?, result: Int) {
        coroutineScope.launch(Dispatchers.Default + findPeerJob) {
            allWatches.emit(
                peers?.map { peerAgent ->
                    Watch(
                        peerAgent.accessory.name,
                        peerAgent.accessory.accessoryId,
                        TizenPlatform.PLATFORM
                    )
                }?.toTypedArray() ?: emptyArray()
            )
        }
    }

    /**
     * Handle a [TizenPlatform.CAPABILITY_MESSAGE].
     * @param peer The [SAPeerAgent] that sent the capability data.
     * @param data The received capability data.
     */
    private fun handleCapabilityMessage(peer: SAPeerAgent, data: ByteArray) {
        coroutineScope.launch(Dispatchers.IO + capabilityJob) {
            capabilityMap[peer.accessory.accessoryId]?.let { flow ->
                if (flow is MutableStateFlow) {
                    flow.emit(String(data, Charsets.UTF_8).split('|').toTypedArray())
                }
            }
        }
    }

    /**
     * Pass a received message on to message listeners and broadcast receivers.
     * @param peer The [SAPeerAgent] that sent the message.
     * @param message The message received.
     * @param data The data received with the message, or null if there was none.
     */
    private fun handleMessage(peer: SAPeerAgent, message: String, data: ByteArray?) {
        val id = Watch.createUUID(
            TizenPlatform.PLATFORM,
            peer.accessory.accessoryId
        )
        messageListeners.forEach { listener ->
            listener.onMessageReceived(
                id,
                message,
                data
            )
        }

        // Send message broadcast
        sendBroadcast(applicationContext, id, message, data)
    }

    companion object {
        private const val TAG = "TizenConnectionHandler"
        private const val OPERATION_TIMEOUT = 1000L
    }
}
