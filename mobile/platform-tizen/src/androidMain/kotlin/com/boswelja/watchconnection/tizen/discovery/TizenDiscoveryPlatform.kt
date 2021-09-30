package com.boswelja.watchconnection.tizen.discovery

import android.content.Context
import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.common.discovery.ConnectionMode
import com.boswelja.watchconnection.core.discovery.DiscoveryPlatform
import com.boswelja.watchconnection.tizen.Constants
import com.boswelja.watchconnection.tizen.TizenAccessoryAgent
import com.boswelja.watchconnection.tizen.discovery.sahelpers.getCapabilitiesDatabase
import com.boswelja.watchconnection.tizen.getTizenAccessoryAgent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

public actual class TizenDiscoveryPlatform(private val context: Context) : DiscoveryPlatform() {

    private val capabilitiesDb = context.getCapabilitiesDatabase()

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

    override fun getCapabilitiesFor(watchId: String): Flow<Set<String>> =
        capabilitiesDb.getCapabilitiesFor(watchId)

    override fun hasCapability(watch: Watch, capability: String): Flow<Boolean> =
        capabilitiesDb.hasCapability(watch.internalId, capability)

    override fun watchesWithCapability(capability: String): Flow<List<Watch>> = capabilitiesDb
        .getPeersWithCapability(capability)
        .map { it.map { id -> Watch("", id, Constants.TIZEN_PLATFORM) } }

    override fun connectionModeFor(
        watch: Watch
    ): Flow<ConnectionMode> = allWatches().map { allWatches ->
        if (allWatches.any { it.internalId == watch.internalId }) ConnectionMode.Bluetooth
        else ConnectionMode.Disconnected
    }

    override suspend fun addLocalCapability(capability: String): Boolean {
        ensureAccessoryAgentLoaded()
        capabilitiesDb.addCapability(accessoryAgent!!.serviceProfileId, capability)
        return true
    }

    override suspend fun removeLocalCapability(capability: String): Boolean {
        ensureAccessoryAgentLoaded()
        capabilitiesDb.removeCapability(accessoryAgent!!.serviceProfileId, capability)
        return true
    }

    private suspend fun ensureAccessoryAgentLoaded() {
        if (accessoryAgent == null) {
            accessoryAgent = context.getTizenAccessoryAgent()
        }
        checkNotNull(accessoryAgent) { "TizenAccessoryAgent failed to initialize" }
    }
}
