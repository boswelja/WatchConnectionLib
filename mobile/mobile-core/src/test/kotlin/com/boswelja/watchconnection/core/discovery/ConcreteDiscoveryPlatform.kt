package com.boswelja.watchconnection.core.discovery

import com.boswelja.watchconnection.common.discovery.Status
import com.boswelja.watchconnection.core.Watch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ConcreteDiscoveryPlatform(
    private val allWatches: List<Watch>,
    private val watchesWithApp: List<Watch>,
    private val capabilities: List<String>,
    private val status: Status
) : DiscoveryPlatform {

    override val platformIdentifier: String = PLATFORM

    override fun allWatches(): Flow<List<Watch>> = flow { emit(allWatches) }

    override fun watchesWithApp(): Flow<List<Watch>> = flow { emit(watchesWithApp) }

    override fun getCapabilitiesFor(watchId: String): Flow<List<String>> =
        flow { emit(capabilities) }

    override fun getStatusFor(watchId: String): Flow<Status> = flow { emit(status) }

    companion object {
        const val PLATFORM = "platform"
    }
}
