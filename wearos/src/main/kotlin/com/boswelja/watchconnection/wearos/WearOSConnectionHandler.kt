package com.boswelja.watchconnection.wearos

import android.content.Context
import com.boswelja.watchconnection.core.MessageListener
import com.boswelja.watchconnection.core.PlatformConnectionHandler
import com.boswelja.watchconnection.core.Watch
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.NodeClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class WearOSConnectionHandler internal constructor(
    private val appCapability: String,
    private val nodeClient: NodeClient,
    private val messageClient: MessageClient,
    private val capabilityClient: CapabilityClient
) : PlatformConnectionHandler {

    /**
     * A [PlatformConnectionHandler] with support for Wear OS via Google's Wearable Support Library.
     * @param context The [Context] to initialize [Wearable] components with.
     * @param appCapability The capability string to use when searching for watches with the
     * companion app installed.
     */
    constructor(
        context: Context,
        appCapability: String
    ) : this(
        appCapability,
        Wearable.getNodeClient(context),
        Wearable.getMessageClient(context),
        Wearable.getCapabilityClient(context)
    )

    private val messageListeners =
        mutableMapOf<MessageListener, MessageClient.OnMessageReceivedListener>()

    override val platformIdentifier = PLATFORM

    override fun allWatches(): Flow<Watch> = flow {
        nodeClient.connectedNodes.await().forEach {
            emit(
                Watch(
                    it.displayName,
                    it.id,
                    PLATFORM
                )
            )
        }
    }

    override fun watchesWithApp(): Flow<Watch> = flow {
        capabilityClient.getCapability(appCapability, CapabilityClient.FILTER_ALL).await().nodes
            .forEach {
                emit(
                    Watch(
                        it.displayName,
                        it.id,
                        PLATFORM
                    )
                )
            }
    }

    override suspend fun getCapabilitiesFor(watchId: String): Flow<String> = flow {
        // Get all capabilities
        val capabilities = capabilityClient
            .getAllCapabilities(CapabilityClient.FILTER_REACHABLE).await()
        capabilities.forEach { capability ->
            // If capability has a node with the same ID as the watch we want, add it
            if (capability.value.nodes.any { it.id == watchId }) {
                emit(capability.key)
            }
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

    override fun registerMessageListener(listener: MessageListener) {
        val onMessageReceiveListener = MessageClient.OnMessageReceivedListener {
            val id = Watch.createUUID(PLATFORM, it.sourceNodeId)
            listener.onMessageReceived(id, it.path, it.data)
        }
        messageClient.addListener(onMessageReceiveListener)
        // Store this in a map, so we can look it up to unregister later
        messageListeners[listener] = onMessageReceiveListener
    }

    override fun unregisterMessageListener(listener: MessageListener) {
        // Look up listener and remove it from both the map and messageClient
        messageListeners.remove(listener)?.let {
            messageClient.removeListener(it)
        }
    }

    companion object {
        const val PLATFORM = "WEAR_OS"
    }
}
