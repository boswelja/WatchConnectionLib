package com.boswelja.watchconnection.tizen

import android.content.Context
import com.boswelja.watchconnection.core.MessageListener
import com.boswelja.watchconnection.core.PlatformConnectionHandler
import com.boswelja.watchconnection.core.Result
import com.boswelja.watchconnection.core.Watch
import com.samsung.android.sdk.SsdkUnsupportedException
import com.samsung.android.sdk.accessory.SA
import com.samsung.android.sdk.accessory.SAAgentV2
import com.samsung.android.sdk.accessory.SAMessage
import com.samsung.android.sdk.accessory.SAPeerAgent
import java.util.UUID
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
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class TizenConnectionHandler internal constructor(
    context: Context,
    accessory: SA
) : PlatformConnectionHandler, SAAgentV2(TAG, context) {

    /**
     * A [PlatformConnectionHandler] with support for Tizen via Samsung's Accessory SDK.
     * @param context The [Context] to initialize [SA] components with.
     */
    constructor(context: Context) : this(
        context,
        SA()
    )

    private val capabilityJob = Job()
    private val findPeerJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val allWatches = MutableStateFlow<Watch?>(null)

    private val idMap = HashMap<String, UUID>()
    private val peerMap = HashMap<String, SAPeerAgent>()
    private val messageListeners = ArrayList<MessageListener>()

    // Keep a map of channels to message IDs to
    private val messageChannelMap = HashMap<Int, Channel<Boolean>>()

    // Keep a map of peer agents to their capabilities
    private val capabilityMap = HashMap<String, Flow<String?>>()

    private val saMessage = object : SAMessage(this) {
        override fun onReceive(peerAgent: SAPeerAgent?, data: ByteArray?) {
            if (peerAgent == null) return
            val id = idMap[peerAgent.accessory.accessoryId] ?: return
            var messageBytes = emptyArray<Byte>()
            var bytes = emptyArray<Byte>()
            var hasMessage = false
            data?.forEach {
                if (!hasMessage) {
                    if (it == messageDelimiter) {
                        hasMessage = true
                    } else {
                        messageBytes += it
                    }
                } else {
                    bytes += it
                }
            }
            val message = String(messageBytes.toByteArray(), Charsets.UTF_8)
            if (message == CAPABILITY_MESSAGE) {
                // Handle capability message
                coroutineScope.launch(Dispatchers.IO + capabilityJob) {
                    capabilityMap[peerAgent.accessory.accessoryId]?.let { flow ->
                        if (flow is MutableStateFlow) {
                            flow.emit(String(bytes.toByteArray(), Charsets.UTF_8))
                        }
                    }
                }
            } else {
                // Pass message on
                messageListeners.forEach { listener ->
                    listener.onMessageReceived(
                        id,
                        message,
                        bytes.toByteArray()
                    )
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
        } catch (e: Exception) {
            // TODO We can't use the Accessory SDK for some reason, handle it
            releaseAgent()
        }
    }

    override val platformIdentifier = PLATFORM

    @ExperimentalCoroutinesApi
    override fun allWatches(): Flow<Watch> {
        findPeerJob.cancel()
        allWatches.resetReplayCache()
        findPeerAgents()
        return allWatches.mapNotNull { it }
    }

    @ExperimentalCoroutinesApi
    override fun watchesWithApp(): Flow<Watch> = allWatches()

    @ExperimentalCoroutinesApi
    override suspend fun getCapabilitiesFor(watchId: String): Flow<String> {
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
        sendMessage(watchId, CAPABILITY_MESSAGE)

        return flow.mapNotNull { it }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    @ExperimentalCoroutinesApi
    override suspend fun sendMessage(watchId: String, message: String, data: ByteArray?): Result {
        val targetAgent = peerMap[watchId] ?: return Result.FAILED
        val bytes = withContext(Dispatchers.Default) {
            return@withContext if (data != null) {
                message.toByteArray(Charsets.UTF_8) + messageDelimiter + data
            } else {
                message.toByteArray(Charsets.UTF_8)
            }
        }

        // Create channel and map message ID to it
        val channel = Channel<Boolean>(capacity = 1)
        val id = saMessage.secureSend(targetAgent, bytes)
        messageChannelMap[id] = channel

        // If channel has sent true, return success
        return if (withTimeoutOrNull(OPERATION_TIMEOUT) { channel.receiveOrNull() } == true) {
            Result.SUCCESS
        } else {
            Result.FAILED
        }
    }

    override fun registerMessageListener(listener: MessageListener) {
        messageListeners.add(listener)
    }

    override fun unregisterMessageListener(listener: MessageListener) {
        messageListeners.remove(listener)
    }

    override fun onFindPeerAgentsResponse(peers: Array<out SAPeerAgent>?, result: Int) {
        coroutineScope.launch(Dispatchers.Default + findPeerJob) {
            peers?.forEach {
                allWatches.emit(
                    Watch(
                        getOrCreateID(it.accessory.accessoryId),
                        it.accessory.name,
                        it.accessory.accessoryId,
                        PLATFORM
                    )
                )
            }
        }
    }

    /**
     * Attempts to get a [UUID] from [idMap], or creates one if it wasn't found
     * @param accessoryId The ID of the Accessory to look up a UUID for.
     * @return An existing [UUID], or a new one if none were found.
     */
    private fun getOrCreateID(accessoryId: String): UUID {
        val id = idMap[accessoryId]
        return if (id != null) {
            id
        } else {
            val newUuid = UUID.randomUUID()
            idMap[accessoryId] = newUuid
            newUuid
        }
    }

    companion object {
        private const val TAG = "TizenConnectionHandler"
        private const val messageDelimiter = Byte.MAX_VALUE
        private const val OPERATION_TIMEOUT = 1000L

        const val CAPABILITY_MESSAGE = "/request_capabilities"
        const val PLATFORM = "TIZEN"
    }
}
