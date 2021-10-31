package com.boswelja.watchconnection.wear.discovery

import android.content.Context
import com.boswelja.watchconnection.common.Phone
import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.common.discovery.Capabilities
import com.boswelja.watchconnection.common.discovery.ConnectionMode
import com.boswelja.watchconnection.common.discovery.DiscoveryClient
import com.boswelja.watchconnection.wear.repeating
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

public actual class DiscoveryClient(context: Context) : DiscoveryClient {

    private val nodeClient = Wearable.getNodeClient(context.applicationContext)
    private val capabilityClient = Wearable.getCapabilityClient(context.applicationContext)

    override suspend fun getCapabilitiesFor(targetUid: String): Set<String> {
        val allCapabilities = capabilityClient
            .getAllCapabilities(CapabilityClient.FILTER_ALL)
            .await()
        return allCapabilities.values
            .filter { capabilityInfo -> capabilityInfo.nodes.any { it.id == targetUid } }
            .map { it.name }
            .toSet()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun hasCapability(
        targetUid: String,
        capability: String
    ): Flow<Boolean> = callbackFlow {
        // Create the listener
        val listener = CapabilityClient.OnCapabilityChangedListener { info ->
            val hasCapability = info.nodes.any { it.id == targetUid }
            trySend(hasCapability)
        }

        // Register the listener
        capabilityClient.addListener(listener, capability)

        // Check capability now
        val hasCapability = capabilityClient
            .getCapability(capability, CapabilityClient.FILTER_ALL)
            .await()
            .nodes
            .any { it.id == targetUid }
        send(hasCapability)

        // Unregister the listener on close
        awaitClose {
            capabilityClient.removeListener(listener)
        }
    }

    override fun connectionModeFor(targetUid: String): Flow<ConnectionMode> = flow {
        repeating(2000L) {
            val connectedNodes = nodeClient.connectedNodes.await()
            val node = connectedNodes.firstOrNull { it.id == targetUid }
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
        } catch (e: Exception) {
            // If we throw an exception, no changes were made
            false
        }
    }

    override suspend fun removeLocalCapability(capability: String): Boolean {
        return try {
            capabilityClient.removeLocalCapability(capability).await()
            true
        } catch (e: Exception) {
            // If we throw an exception, no changes were made
            false
        }
    }

    public actual suspend fun pairedPhone(): Phone? {
        // Try look up phone via capability
        val capableNode = capabilityClient
            .getCapability(Capabilities.ConnectionLibHost, CapabilityClient.FILTER_ALL)
            .await()
            .nodes.firstOrNull() ?: return null

        return Phone(
            capableNode.id,
            capableNode.displayName
        )
    }

    public actual suspend fun localWatch(): Watch {
        val node = nodeClient.localNode.await()
        return Watch(
            node.displayName,
            node.id,
            ""
        )
    }

    public actual suspend fun allPhoneCapabilities(): Set<String> {
        val pairedPhone = pairedPhone() ?: return emptySet()
        val allCapabilities = capabilityClient
            .getAllCapabilities(CapabilityClient.FILTER_ALL)
            .await()

        return allCapabilities.values
            .filter { capabilityInfo -> capabilityInfo.nodes.any { it.id == pairedPhone.uid } }
            .map { it.name }
            .toSet()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    public actual suspend fun phoneHasCapability(
        capability: String
    ): Flow<Boolean> = hasCapability(
        pairedPhone()!!.uid,
        capability
    )

    public actual fun connectionMode(): Flow<ConnectionMode> = flow {
        val phone = pairedPhone() ?: return@flow
        emitAll(connectionModeFor(phone.uid))
    }
}
