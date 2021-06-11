package com.boswelja.watchconnection.wearos

import android.content.Context
import com.boswelja.watchconnection.core.Message
import com.boswelja.watchconnection.core.Status
import com.boswelja.watchconnection.core.Watch
import com.boswelja.watchconnection.core.WatchPlatform
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.NodeClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class WearOSPlatform constructor(
    private val appCapability: String,
    private val capabilities: List<String>,
    private val nodeClient: NodeClient,
    private val messageClient: MessageClient,
    private val capabilityClient: CapabilityClient
) : WatchPlatform {

    /**
     * A [WatchPlatform] with support for Wear OS via Google's Wearable Support Library.
     * @param context The [Context] to initialize [Wearable] components with.
     * @param appCapability The capability string to use when searching for watches with the app.
     * @param capabilities A list of capability strings to use when searching for watch capabilities
     * companion app installed.
     */
    constructor(
        context: Context,
        appCapability: String,
        capabilities: List<String>
    ) : this(
        appCapability,
        capabilities,
        Wearable.getNodeClient(context),
        Wearable.getMessageClient(context),
        Wearable.getCapabilityClient(context)
    )

    private val coroutineScope = MainScope()

    private val wearableMessageReceiver = MessageClient.OnMessageReceivedListener { messageEvent ->
        val message = Message(
            Watch.createUUID(PLATFORM, messageEvent.sourceNodeId),
            messageEvent.path,
            messageEvent.data
        )
        incomingMessageFlow.tryEmit(message)
    }

    private val incomingMessageFlow = MutableSharedFlow<Message>(
        replay = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val platformIdentifier = PLATFORM

    override val incomingMessages: Flow<Message> = incomingMessageFlow

    init {
        coroutineScope.launch {
            incomingMessageFlow.subscriptionCount.collect { subscriberCount ->
                if (subscriberCount > 0) {
                    messageClient.addListener(wearableMessageReceiver)
                } else {
                    messageClient.removeListener(wearableMessageReceiver)
                }
            }
        }
    }
    override fun allWatches(): Flow<List<Watch>> = flow {
        emit(
            nodeClient.connectedNodes.await().map { node ->
                Watch(
                    node.displayName,
                    node.id,
                    PLATFORM
                )
            }
        )
    }

    @ExperimentalCoroutinesApi
    override fun watchesWithApp(): Flow<List<Watch>> = callbackFlow {
        // Create capability listener
        val listener = CapabilityClient.OnCapabilityChangedListener { info ->
            trySend(
                info.nodes.map { node ->
                    Watch(
                        node.displayName,
                        node.id,
                        PLATFORM
                    )
                }
            )
        }
        // Add listener
        capabilityClient.addListener(listener, appCapability)

        // Update capabilities now
        val capabilityInfo = capabilityClient
            .getCapability(appCapability, CapabilityClient.FILTER_ALL).await()
        send(
            capabilityInfo.nodes.map { node ->
                Watch(
                    node.displayName,
                    node.id,
                    PLATFORM
                )
            }
        )

        // Remove listener on Flow close
        awaitClose {
            capabilityClient.removeListener(listener)
        }
    }

    override fun getCapabilitiesFor(watchId: String): Flow<List<String>> = flow {
        val discoveredCapabilities = mutableListOf<String>()
        capabilities.forEach { capability ->
            // Get capability info
            val capabilityInfo = capabilityClient
                .getCapability(capability, CapabilityClient.FILTER_ALL)
                .await()
            // If node is found with same ID as watch, emit capability
            if (capabilityInfo.nodes.any { it.id == watchId })
                discoveredCapabilities += capabilityInfo.name
        }
        emit(discoveredCapabilities)
    }

    @ExperimentalCoroutinesApi
    override fun getStatusFor(watchId: String): Flow<Status> = callbackFlow {
        // Start with CONNECTING
        send(Status.CONNECTING)

        // Create our listener
        val listener = CapabilityClient.OnCapabilityChangedListener { info: CapabilityInfo ->
            getStatusFromCapabilityInfo(watchId, info)
        }
        // Add the listener
        capabilityClient.addListener(listener, appCapability)

        // Update capabilities now
        val capabilityInfo = capabilityClient
            .getCapability(appCapability, CapabilityClient.FILTER_ALL).await()
        getStatusFromCapabilityInfo(watchId, capabilityInfo)

        // On finish, remove the listener
        awaitClose {
            capabilityClient.removeListener(listener)
        }
    }

    override suspend fun sendMessage(watchId: String, message: String, data: ByteArray?): Boolean {
        // Either sendMessage is successful, or ApiException is thrown
        return try {
            messageClient.sendMessage(watchId, message, data).await()
            // If we get here, message send was successful
            true
        } catch (e: ApiException) {
            false
        }
    }

    @ExperimentalCoroutinesApi
    private fun ProducerScope<Status>.getStatusFromCapabilityInfo(
        watchId: String,
        info: CapabilityInfo
    ) {
        // If watch is found in capable nodes list, check if it's connected
        if (info.nodes.any { it.id == watchId }) {
            try {
                // runBlocking should be safe here, since we're within a Flow
                val connectedNodes = runBlocking { nodeClient.connectedNodes.await() }
                // Got connected nodes, check if it contains our desired node
                if (connectedNodes.any { it.id == watchId }) trySend(Status.CONNECTED)
                else trySend(Status.DISCONNECTED)
            } catch (e: CancellationException) {
                // Failed, send error
                trySend(Status.ERROR)
            }
        } else {
            // No watch in capable nodes, app is missing
            trySend(Status.MISSING_APP)
        }
    }

    companion object {
        const val PLATFORM = "WEAR_OS"
    }
}
