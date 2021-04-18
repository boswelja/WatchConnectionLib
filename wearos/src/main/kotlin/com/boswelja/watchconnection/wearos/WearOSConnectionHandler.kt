package com.boswelja.watchconnection.wearos

import android.content.Context
import com.boswelja.watchconnection.core.MessageListener
import com.boswelja.watchconnection.core.Result
import com.boswelja.watchconnection.core.Watch
import com.boswelja.watchconnection.core.PlatformConnectionHandler
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.NodeClient
import com.google.android.gms.wearable.Wearable
import java.util.UUID
import kotlinx.coroutines.tasks.await

class WearOSConnectionHandler internal constructor(
    context: Context,
    private val nodeClient: NodeClient,
    private val messageClient: MessageClient
) : PlatformConnectionHandler(context) {

    constructor(context: Context) : this(
        context,
        Wearable.getNodeClient(context),
        Wearable.getMessageClient(context)
    )

    private val messageListeners = mutableMapOf<MessageListener, MessageClient.OnMessageReceivedListener>()
    private val _availableWatches = ArrayList<Watch>()

    override val platformIdentifier = PLATFORM

    override val availableWatches: List<Watch> = _availableWatches

    override suspend fun sendMessage(watchId: String, message: String, data: ByteArray?): Result {
        // Either sendMessage is successful, or ApiException is thrown
        return try {
            messageClient.sendMessage(watchId, message, data).await()
            Result.SUCCESS
        } catch (e: ApiException) {
            Result.FAILED
        }
    }

    override suspend fun registerMessageListener(listener: MessageListener) {
        val onMessageReceiveListener = MessageClient.OnMessageReceivedListener {
            _availableWatches.firstOrNull { watch ->
                watch.platformId == it.sourceNodeId
            }?.let { watch ->
                listener.onMessageReceived(watch.id, it.path, it.data)
            }
        }
        messageClient.addListener(onMessageReceiveListener)
        // Store this in a map, so we can look it up to unregister later
        messageListeners[listener] = onMessageReceiveListener
    }

    override suspend fun unregisterMessageListener(listener: MessageListener) {
        // Look up listener and remove it from both the map and messageClient
        messageListeners.remove(listener)?.let {
            messageClient.removeListener(it)
        }
    }

    override suspend fun refreshData(): Result {
        refreshConnectedNodes()
        return Result.SUCCESS
    }

    private suspend fun refreshConnectedNodes() {
        val result = nodeClient.connectedNodes.await()
        result.filter { _availableWatches.any { watch -> watch.platformId == it.id} }.forEach {
            val newWatch = Watch(
                UUID.randomUUID(),
                it.id,
                it.displayName,
                PLATFORM
            )
            _availableWatches.add(newWatch)
        }
    }

    companion object {
        const val PLATFORM = "WEAR_OS"
    }
}