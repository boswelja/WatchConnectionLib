package com.boswelja.watchconnection.wearos

import android.content.Context
import com.boswelja.watchconnection.core.Watch
import com.boswelja.watchconnection.core.WatchConnectionInterface
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.NodeClient
import com.google.android.gms.wearable.Wearable
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class WearOSConnectionInterface internal constructor(
    private val nodeClient: NodeClient,
    private val messageClient: MessageClient,
    private val coroutineScope: CoroutineScope
) : WatchConnectionInterface {

    constructor(context: Context) : this(
        Wearable.getNodeClient(context),
        Wearable.getMessageClient(context),
        CoroutineScope(Dispatchers.IO)
    )

    private val _availableWatches = ArrayList<Watch>()

    override val platformIdentifier = PLATFORM

    override val availableWatches: List<Watch> = _availableWatches

    override suspend fun sendMessage(watchId: String, path: String, data: ByteArray?) {
        messageClient.sendMessage(watchId, path, data)
    }

    override suspend fun refreshData() {
        refreshConnectedNodes()
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