package com.boswelja.watchconnection.tizen

import android.content.Context
import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.common.discovery.ConnectionMode
import com.boswelja.watchconnection.common.discovery.Status
import com.boswelja.watchconnection.core.discovery.DiscoveryPlatform
import com.samsung.android.sdk.accessory.SAAgentV2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

public class TizenDiscoveryPlatform(context: Context) : DiscoveryPlatform() {

    override val platformIdentifier: String = Constants.TIZEN_PLATFORM

    private lateinit var accessoryAgent: TizenAccessoryAgent
    public var isReady: Boolean = false
        private set

    init {
        SAAgentV2.requestAgent(
            context,
            TizenAccessoryAgent::class.java.name,
            object : SAAgentV2.RequestAgentCallback {
                override fun onAgentAvailable(agent: SAAgentV2?) {
                    if (agent is TizenAccessoryAgent) {
                        accessoryAgent = agent
                        isReady = true
                    } else {
                        throw Exception("Agent provided was not our agent")
                    }
                }

                override fun onError(errorCode: Int, message: String?) {
                    throw Exception(message)
                }
            }
        )
    }

    override fun allWatches(): Flow<List<Watch>> = accessoryAgent.allWatches()

    override fun watchesWithApp(): Flow<List<Watch>> = accessoryAgent.allWatches()

    override fun getCapabilitiesFor(watchId: String): Flow<List<String>> =
        accessoryAgent.getCapabilitiesFor(watchId)

    override fun hasCapability(watch: Watch, capability: String): Flow<Boolean> =
        accessoryAgent.getCapabilitiesFor(watch.internalId).map { it.contains(capability) }

    override fun getStatusFor(watchId: String): Flow<Status> = allWatches().map { allWatches ->
        if (allWatches.any { it.internalId == watchId }) Status.CONNECTED
        else Status.DISCONNECTED
    }

    override fun connectionModeFor(
        watch: Watch
    ): Flow<ConnectionMode> = allWatches().map { allWatches ->
        if (allWatches.any { it.internalId == watch.internalId }) ConnectionMode.Bluetooth
        else ConnectionMode.Disconnected
    }
}
