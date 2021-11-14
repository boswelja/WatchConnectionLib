package com.boswelja.watchconnection.core.discovery

import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.common.discovery.ConnectionMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ConcreteDiscoveryPlatform(
    var allWatches: MutableList<Watch>,
    var capabilities: MutableSet<String>,
    var connectionMode: ConnectionMode,
    platform: String
) : DiscoveryPlatform() {

    val localCapabilities = mutableSetOf<String>()

    override val platformIdentifier: String = platform

    override fun allWatches(): Flow<List<Watch>> = MutableStateFlow(allWatches)

    override suspend fun getCapabilitiesFor(watchId: String): Set<String> = capabilities

    override fun watchHasCapability(watchId: String, capability: String): Flow<Boolean> =
        MutableStateFlow(capabilities.contains(capability))

    override fun watchesWithCapability(capability: String): Flow<List<Watch>> = allWatches()

    override fun connectionModeFor(watchId: String): Flow<ConnectionMode> =
        MutableStateFlow(connectionMode)

    override suspend fun addLocalCapability(capability: String): Boolean {
        return localCapabilities.add(capability)
    }

    override suspend fun removeLocalCapability(capability: String): Boolean {
        return localCapabilities.remove(capability)
    }
}

internal fun createPlatforms(count: Int): List<ConcreteDiscoveryPlatform> {
    return (0 until count).map { id ->
        ConcreteDiscoveryPlatform(
            mutableListOf(),
            mutableSetOf(),
            ConnectionMode.Disconnected,
            "platform$id"
        )
    }
}
