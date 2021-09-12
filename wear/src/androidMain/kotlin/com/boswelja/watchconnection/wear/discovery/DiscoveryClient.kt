package com.boswelja.watchconnection.wear.discovery

import android.content.Context
import android.net.Uri
import com.boswelja.watchconnection.common.Phone
import com.boswelja.watchconnection.wear.repeating
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

public actual class DiscoveryClient(context: Context) {

    private val nodeClient = Wearable.getNodeClient(context.applicationContext)
    private val capabilityClient = Wearable.getCapabilityClient(context.applicationContext)

    public actual suspend fun addCapability(capability: String) {
        capabilityClient.addLocalCapability(capability).await()
    }

    public actual suspend fun pairedPhone(): Phone {
        val node = nodeClient.connectedNodes.await().first { it.isNearby }
        return Phone(
            node.displayName,
            node.id
        )
    }

    public actual suspend fun removeCapability(capability: String) {
        capabilityClient.removeLocalCapability(capability).await()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    public actual fun phoneCapabilities(): Flow<Set<String>> = callbackFlow {
        // Get the paired phone and create a set to track capabilities
        val phone = pairedPhone()
        val capabilities = mutableSetOf<String>()

        // Create a listener to handle capability changes
        val listener = CapabilityClient.OnCapabilityChangedListener { info ->
            val hasCapability = info.nodes.any { it.id == phone.uid }
            val wasChanged = if (hasCapability) {
                capabilities.add(info.name)
            } else {
                capabilities.remove(info.name)
            }
            if (wasChanged) trySend(capabilities)
        }

        // Build a Uri for matching capability changes from just the connected phone
        val uri = Uri.Builder()
            .scheme("wear")
            .authority(phone.uid)
            .path("/*")
            .build()

        // Add the listener
        capabilityClient.addListener(listener, uri, CapabilityClient.FILTER_LITERAL)

        // Remove the listener on close
        awaitClose {
            capabilityClient.removeListener(listener)
        }
    }

    public actual fun phoneConnected(): Flow<Boolean> = flow {
        val phone = pairedPhone()
        repeating(2000L) {
            val connected = nodeClient.connectedNodes.await().any { it.id == phone.uid }
            emit(connected)
        }
    }
}
