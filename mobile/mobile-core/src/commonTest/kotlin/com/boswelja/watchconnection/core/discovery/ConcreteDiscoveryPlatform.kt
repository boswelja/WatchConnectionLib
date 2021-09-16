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

    override val platformIdentifier: String = PLATFORM

    override fun allWatches(): Flow<List<Watch>> = flowOf(allWatches)

    override fun getCapabilitiesFor(watchId: String): Flow<Set<String>> = flowOf(capabilities)

    override fun hasCapability(watch: Watch, capability: String): Flow<Boolean> =
        flow { emit(capabilities.contains(capability)) }

    override fun watchesWithCapability(capability: String): Flow<List<Watch>> = allWatches()

    override fun connectionModeFor(watch: Watch): Flow<ConnectionMode> = flowOf(connectionMode)

    companion object {
        const val PLATFORM = "platform"
    }
}
