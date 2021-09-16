package com.boswelja.watchconnection.core.discovery

import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.common.discovery.ConnectionMode
import com.boswelja.watchconnection.common.discovery.Status
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class ConcreteDiscoveryPlatform(
    private val allWatches: List<Watch>,
    private val watchesWithApp: List<Watch>,
    private val capabilities: List<String>,
    private val status: Status,
    private val connectionMode: ConnectionMode
) : DiscoveryPlatform() {

    override val platformIdentifier: String = PLATFORM

    override fun allWatches(): Flow<List<Watch>> = flowOf(allWatches)

    override fun watchesWithApp(): Flow<List<Watch>> = flowOf(watchesWithApp)

    override fun getCapabilitiesFor(watchId: String): Flow<List<String>> = flowOf(capabilities)

    override fun hasCapability(watch: Watch, capability: String): Flow<Boolean> =
        flow { emit(capabilities.contains(capability)) }

    override fun getStatusFor(watchId: String): Flow<Status> = flowOf(status)
    override fun connectionModeFor(watch: Watch): Flow<ConnectionMode> = flowOf(connectionMode)

    companion object {
        const val PLATFORM = "platform"
    }
}
