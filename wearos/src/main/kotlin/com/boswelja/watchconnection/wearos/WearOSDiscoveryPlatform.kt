package com.boswelja.watchconnection.wearos

import android.content.Context
import com.boswelja.watchconnection.core.Watch
import com.boswelja.watchconnection.core.discovery.DiscoveryPlatform
import com.boswelja.watchconnection.core.discovery.Status
import com.boswelja.watchconnection.wearos.Constants.WEAROS_PLATFORM
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.NodeClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class WearOSDiscoveryPlatform(
    private val appCapability: String,
    private val capabilities: List<String>,
    private val nodeClient: NodeClient,
    private val capabilityClient: CapabilityClient
) : DiscoveryPlatform {

    constructor(
        context: Context,
        appCapability: String,
        capabilities: List<String>
    ) : this(
        appCapability,
        capabilities,
        Wearable.getNodeClient(context),
        Wearable.getCapabilityClient(context)
    )

    override val platformIdentifier: String = WEAROS_PLATFORM

    override fun allWatches(): Flow<List<Watch>> = flow {
        emit(
            nodeClient.connectedNodes.await().map { node ->
                Watch(
                    node.displayName,
                    node.id,
                    platformIdentifier
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
                        platformIdentifier
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
                    platformIdentifier
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
                val node = connectedNodes.firstOrNull { it.id == watchId }
                val status = node?.let {
                    if (node.isNearby) Status.CONNECTED_NEARBY
                    else Status.CONNECTED
                } ?: Status.DISCONNECTED
                trySend(status)
            } catch (e: CancellationException) {
                // Failed, send error
                trySend(Status.ERROR)
            }
        } else {
            // No watch in capable nodes, app is missing
            trySend(Status.MISSING_APP)
        }
    }
}
