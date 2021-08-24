package com.boswelja.watchconnection.tizen

import android.content.Context
import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.common.discovery.Status
import com.boswelja.watchconnection.core.discovery.DiscoveryPlatform
import com.samsung.android.sdk.accessory.SAAgentV2
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TizenDiscoveryPlatform(context: Context) : DiscoveryPlatform {

    override val platformIdentifier: String = Constants.TIZEN_PLATFORM

    private lateinit var accessoryAgent: TizenAccessoryAgent
    var isReady: Boolean = false
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

    @ExperimentalCoroutinesApi
    override fun allWatches(): Flow<List<Watch>> = accessoryAgent.allWatches()

    @ExperimentalCoroutinesApi
    override fun watchesWithApp(): Flow<List<Watch>> = accessoryAgent.allWatches()

    @ExperimentalCoroutinesApi
    override fun getCapabilitiesFor(watchId: String): Flow<List<String>> =
        accessoryAgent.getCapabilitiesFor(watchId)

    @ExperimentalCoroutinesApi
    override fun getStatusFor(watchId: String): Flow<Status> = allWatches().map { allWatches ->
        if (allWatches.any { it.platformId == watchId }) Status.CONNECTED
        else Status.DISCONNECTED
    }
}
