package com.boswelja.watchconnection.wearos

import android.content.Context
import com.boswelja.watchconnection.core.MessageListener
import com.boswelja.watchconnection.core.Watch
import com.boswelja.watchconnection.core.WatchPlatform
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.NodeClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
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

    private val messageListeners =
        mutableMapOf<MessageListener, MessageClient.OnMessageReceivedListener>()

    override val platformIdentifier = PLATFORM

    override fun allWatches(): Flow<Array<Watch>> = flow {
        emit(
            nodeClient.connectedNodes.await().map { node ->
                Watch(
                    node.displayName,
                    node.id,
                    PLATFORM
                )
            }.toTypedArray()
        )
    }

    @ExperimentalCoroutinesApi
    override fun watchesWithApp(): Flow<Array<Watch>> = callbackFlow {
        // Create capability listener
        val listener = CapabilityClient.OnCapabilityChangedListener { info ->
            sendBlocking(
                info.nodes.map { node ->
                    Watch(
                        node.displayName,
                        node.id,
                        PLATFORM
                    )
                }.toTypedArray()
            )
        }
        // Add listener
        capabilityClient.addListener(listener, appCapability)

        // Remove listener on Flow close
        awaitClose {
            capabilityClient.removeListener(listener)
        }
    }

    override fun getCapabilitiesFor(watchId: String): Flow<Array<String>> = flow {
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
        emit(discoveredCapabilities.toTypedArray())
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

    override fun addMessageListener(listener: MessageListener) {
        val onMessageReceiveListener = MessageClient.OnMessageReceivedListener {
            val id = Watch.createUUID(PLATFORM, it.sourceNodeId)
            listener.onMessageReceived(id, it.path, it.data)
        }
        messageClient.addListener(onMessageReceiveListener)
        // Store this in a map, so we can look it up to unregister later
        messageListeners[listener] = onMessageReceiveListener
    }

    override fun removeMessageListener(listener: MessageListener) {
        // Look up listener and remove it from both the map and messageClient
        messageListeners.remove(listener)?.let {
            messageClient.removeListener(it)
        }
    }

    companion object {
        const val PLATFORM = "WEAR_OS"
    }
}
