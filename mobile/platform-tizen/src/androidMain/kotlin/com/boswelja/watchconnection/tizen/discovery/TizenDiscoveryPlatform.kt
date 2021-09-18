package com.boswelja.watchconnection.tizen.discovery

import android.content.Context
import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.common.discovery.ConnectionMode
import com.boswelja.watchconnection.core.discovery.DiscoveryPlatform
import com.boswelja.watchconnection.tizen.Constants
import com.boswelja.watchconnection.tizen.TizenAccessoryAgent
import com.boswelja.watchconnection.tizen.getTizenAccessoryAgent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

public actual class TizenDiscoveryPlatform(private val context: Context) : DiscoveryPlatform() {

    override val platformIdentifier: String = Constants.TIZEN_PLATFORM

    private var accessoryAgent: TizenAccessoryAgent? = null

    override fun allWatches(): Flow<List<Watch>> = flow {
        ensureAccessoryAgentLoaded()
        val mappedFlow = accessoryAgent!!.foundPeerAgents
            .map {
                it.map { peer ->
                    Watch(
                        peer.accessory.name,
                        peer.peerId,
                        Constants.TIZEN_PLATFORM
                    )
                }
            }
        emitAll(mappedFlow)
    }

    override fun getCapabilitiesFor(watchId: String): Flow<Set<String>> = flowOf(setOf())

    override fun hasCapability(watch: Watch, capability: String): Flow<Boolean> = flowOf(false)

    override fun watchesWithCapability(capability: String): Flow<List<Watch>> = flowOf(listOf())

    override fun connectionModeFor(
        watch: Watch
    ): Flow<ConnectionMode> = allWatches().map { allWatches ->
        if (allWatches.any { it.internalId == watch.internalId }) ConnectionMode.Bluetooth
        else ConnectionMode.Disconnected
    }

    private suspend fun ensureAccessoryAgentLoaded() {
        if (accessoryAgent == null) {
            accessoryAgent = context.getTizenAccessoryAgent()
        }
        checkNotNull(accessoryAgent) { "TizenAccessoryAgent failed to initialize" }
    }
}
