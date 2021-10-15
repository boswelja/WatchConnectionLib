package com.boswelja.watchconnection.core.discovery

import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.common.discovery.ConnectionMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class ConcreteDiscoveryPlatform(
    private val allWatches: List<Watch>,
    private val capabilities: Set<String>,
    private val connectionMode: ConnectionMode
) : DiscoveryPlatform() {

    val localCapabilities = mutableListOf<String>()

    override val platformIdentifier: String = PLATFORM

    override fun allWatches(): Flow<List<Watch>> = flowOf(allWatches)

    override suspend fun getCapabilitiesFor(watchId: String): Set<String> = capabilities

    override fun watchHasCapability(watchId: String, capability: String): Flow<Boolean> =
        flow { emit(capabilities.contains(capability)) }

    override fun watchesWithCapability(capability: String): Flow<List<Watch>> = allWatches()

    override fun connectionModeFor(watchId: String): Flow<ConnectionMode> = flowOf(connectionMode)

    override suspend fun addLocalCapability(capability: String): Boolean {
        return localCapabilities.add(capability)
    }

    override suspend fun removeLocalCapability(capability: String): Boolean {
        return localCapabilities.remove(capability)
    }

    companion object {
        const val PLATFORM = "platform"
    }
}
