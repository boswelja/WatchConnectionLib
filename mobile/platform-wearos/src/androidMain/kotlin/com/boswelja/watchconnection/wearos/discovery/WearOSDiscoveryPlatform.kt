package com.boswelja.watchconnection.wearos.discovery

import android.content.Context
import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.common.discovery.ConnectionMode
import com.boswelja.watchconnection.core.discovery.DiscoveryPlatform
import com.boswelja.watchconnection.wearos.Constants.WEAROS_PLATFORM
import com.boswelja.watchconnection.wearos.repeating
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.NodeClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

/**
 * A [DiscoveryPlatform] implementation for Wear OS.
 */
public actual class WearOSDiscoveryPlatform(
    private val nodeClient: NodeClient,
    private val capabilityClient: CapabilityClient,
    private val scanRepeatInterval: Long = 2000
) : DiscoveryPlatform() {

    public constructor(
        context: Context,
        scanRepeatInterval: Long = 2000
    ) : this(
        Wearable.getNodeClient(context),
        Wearable.getCapabilityClient(context),
        scanRepeatInterval
    )

    override val platformIdentifier: String = WEAROS_PLATFORM

    override fun allWatches(): Flow<List<Watch>> = flow {
        repeating(interval = scanRepeatInterval) {
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
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getCapabilitiesFor(watchId: String): Set<String> {
        return capabilityClient
            .getAllCapabilities(CapabilityClient.FILTER_ALL)
            .await()
            .values
            .filter { it.nodes.any { node -> node.id == watchId } }
            .map { it.name }
            .toSet()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun watchHasCapability(watchId: String, capability: String): Flow<Boolean> = callbackFlow {
        val listener = CapabilityClient.OnCapabilityChangedListener { capabilityInfo ->
            trySend(capabilityInfo.nodes.any { it.id == watchId })
        }

        // Get the capability info immediately
        val hasCapability = capabilityClient
            .getCapability(capability, CapabilityClient.FILTER_ALL)
            .await()
            .nodes
            .any { it.id == watchId }
        send(hasCapability)

        capabilityClient.addListener(listener, capability)

        awaitClose {
            capabilityClient.removeListener(listener)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun watchesWithCapability(capability: String): Flow<List<Watch>> = callbackFlow {
        val listener = CapabilityClient.OnCapabilityChangedListener { capabilityInfo ->
            val watches = capabilityInfo.nodes.map { node ->
                Watch(node.displayName, node.id, WEAROS_PLATFORM)
            }
            trySend(watches)
        }

        // Get the capability info immediately
        val watchesWithCapability = capabilityClient
            .getCapability(capability, CapabilityClient.FILTER_ALL)
            .await()
            .nodes
            .map { node ->
                Watch(node.displayName, node.id, WEAROS_PLATFORM)
            }
        send(watchesWithCapability)

        capabilityClient.addListener(listener, capability)

        awaitClose {
            capabilityClient.removeListener(listener)
        }
    }

    override fun connectionModeFor(watchId: String): Flow<ConnectionMode> = flow {
        repeating(scanRepeatInterval) {
            val connectedNodes = nodeClient.connectedNodes.await()
            val node = connectedNodes.firstOrNull { it.id == watchId }
            val connectionMode = node?.let {
                // If NodeClient considers the node to be nearby, assume a bluetooth connection
                if (it.isNearby) ConnectionMode.Bluetooth else ConnectionMode.Internet
            } ?: ConnectionMode.Disconnected
            emit(connectionMode)
        }
    }

    override suspend fun addLocalCapability(capability: String): Boolean {
        return try {
            capabilityClient.addLocalCapability(capability).await()
            true
        } catch (_: Exception) {
            // If we throw an exception, no changes were made
            false
        }
    }

    override suspend fun removeLocalCapability(capability: String): Boolean {
        return try {
            capabilityClient.removeLocalCapability(capability).await()
            true
        } catch (_: Exception) {
            // If we throw an exception, no changes were made
            false
        }
    }
}
