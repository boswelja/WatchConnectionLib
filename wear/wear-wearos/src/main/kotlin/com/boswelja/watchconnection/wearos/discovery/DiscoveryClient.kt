package com.boswelja.watchconnection.wearos.discovery

import android.content.Context
import com.boswelja.watchconnection.common.discovery.Status
import com.boswelja.watchconnection.core.Phone
import com.boswelja.watchconnection.core.discovery.BaseDiscoveryClient
import com.boswelja.watchconnection.wearos.repeating
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

/**
 * A [BaseDiscoveryClient] for Wear OS watches.
 * @param context [Context].
 * @param appCapability The local app capability.
 * @param capabilities The list of expected capabilities.
 * @param scanRepeatInterval The interval at which to repeat scans.
 */
class DiscoveryClient(
    context: Context,
    private val appCapability: String,
    private val capabilities: List<String>,
    private val scanRepeatInterval: Long = 5000
) : BaseDiscoveryClient() {

    private val nodeClient = Wearable.getNodeClient(context.applicationContext)
    private val capabilityClient = Wearable.getCapabilityClient(context.applicationContext)

    override suspend fun pairedPhone(): Phone {
        val node = nodeClient.connectedNodes.await().first()
        return Phone(
            node.displayName,
            node.id
        )
    }

    override suspend fun addCapability(capability: String) {
        capabilityClient.addLocalCapability(capability).await()
    }

    override suspend fun removeCapability(capability: String) {
        capabilityClient.removeLocalCapability(capability).await()
    }

    override fun phoneCapabilities(): Flow<List<String>> = flow {
        val phone = pairedPhone()
        repeating(interval = scanRepeatInterval) {
            val discoveredCapabilities = mutableListOf<String>()
            capabilities.forEach { capability ->
                // Get capability info
                val capabilityInfo = capabilityClient
                    .getCapability(capability, CapabilityClient.FILTER_ALL)
                    .await()
                // If node is found with same ID as watch, emit capability
                if (capabilityInfo.nodes.any { it.id == phone.internalId })
                    discoveredCapabilities += capabilityInfo.name
            }
            emit(discoveredCapabilities)
        }
    }

    override fun phoneStatus(): Flow<Status> = flow {
        val phone = pairedPhone()
        // Start with CONNECTING
        emit(Status.CONNECTING)

        // Get status at a set interval
        repeating(interval = scanRepeatInterval) {
            val capabilityInfo = capabilityClient
                .getCapability(appCapability, CapabilityClient.FILTER_ALL).await()
            if (capabilityInfo.nodes.any { it.id == phone.internalId }) {
                try {
                    // runBlocking should be safe here, since we're within a Flow
                    val connectedNodes = nodeClient.connectedNodes.await()
                    // Got connected nodes, check if it contains our desired node
                    val node = connectedNodes.firstOrNull { it.id == phone.internalId }
                    val status = node?.let {
                        if (node.isNearby) Status.CONNECTED_NEARBY
                        else Status.CONNECTED
                    } ?: Status.DISCONNECTED
                    emit(status)
                } catch (e: CancellationException) {
                    // Failed, send error
                    emit(Status.ERROR)
                }
            } else {
                // No watch in capable nodes, app is missing
                emit(Status.MISSING_APP)
            }
        }
    }
}
