package com.boswelja.watchconnection.wearos

import android.content.Context
import com.boswelja.watchconnection.core.MessageListener
import com.boswelja.watchconnection.core.Result
import com.boswelja.watchconnection.core.Watch
import com.boswelja.watchconnection.core.PlatformConnectionHandler
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.NodeClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import kotlinx.coroutines.tasks.await

class WearOSConnectionHandler internal constructor(
    private val appCapability: String,
    private val nodeClient: NodeClient,
    private val messageClient: MessageClient,
    private val capabilityClient: CapabilityClient
) : PlatformConnectionHandler() {

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

    private val idMap = HashMap<String, UUID>()

    private val messageListeners = mutableMapOf<MessageListener, MessageClient.OnMessageReceivedListener>()

    override val platformIdentifier = PLATFORM

    override fun allWatches(): Flow<Watch> = flow {
        nodeClient.connectedNodes.await().forEach {
            emit(
                Watch(
                    getOrCreateID(it.id),
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
                        getOrCreateID(it.id),
                        it.displayName,
                        it.id,
                        PLATFORM
                    )
                )
            }
    }

    override suspend fun sendMessage(watchId: String, message: String, data: ByteArray?): Result {
        // Either sendMessage is successful, or ApiException is thrown
        return try {
            messageClient.sendMessage(watchId, message, data).await()
            Result.SUCCESS
        } catch (e: ApiException) {
            Result.FAILED
        }
    }

    override fun registerMessageListener(listener: MessageListener) {
        val onMessageReceiveListener = MessageClient.OnMessageReceivedListener {
            idMap[it.sourceNodeId]?.let { id ->
                listener.onMessageReceived(id, it.path, it.data)
            }
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

    /**
     * Attempts to get a [UUID] from [idMap], or creates one if it wasn't found
     * @param nodeId The ID of the Node to look up a UUID for.
     * @return An existing [UUID], or a new one if none were found.
     */
    private fun getOrCreateID(nodeId: String): UUID {
        val id = idMap[nodeId]
        return if (id != null) {
            id
        } else {
            val newUuid = UUID.randomUUID()
            idMap[nodeId] = newUuid
            newUuid
        }
    }

    companion object {
        const val PLATFORM = "WEAR_OS"
    }
}