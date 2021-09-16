package com.boswelja.watchconnection.core.discovery

import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.core.BaseClient
import com.boswelja.watchconnection.core.Platform
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * DiscoveryClient takes a number of [DiscoveryPlatform]s, and provides a common interface between
 * them.
 * @param platforms The [DiscoveryPlatform]s this MessageClient should support.
 */
public class DiscoveryClient(
    platforms: List<DiscoveryPlatform>
) : BaseClient<DiscoveryPlatform>(platforms) {

    /**
     * Get a [Flow] of all [Watch]es found by all [Platform]s.
     */
    @ExperimentalCoroutinesApi
    public fun allWatches(): Flow<List<Watch>> =
        combine(platforms.values.map { it.allWatches() }) { flows ->
            val list = mutableListOf<Watch>()
            flows.forEach { watches ->
                list += watches
            }
            list
        }

    /**
     * Get a flow of capabilities found for a given [Watch].
     * @param watch See [Watch].
     * @return A [Flow] of capability strings declared by the watch.
     */
    public fun getCapabilitiesFor(watch: Watch): Flow<List<String>>? {
        return platforms[watch.platform]?.getCapabilitiesFor(watch.internalId)
    }
}
