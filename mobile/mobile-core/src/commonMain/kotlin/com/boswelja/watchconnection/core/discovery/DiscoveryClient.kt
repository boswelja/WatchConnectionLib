package com.boswelja.watchconnection.core.discovery

import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.common.discovery.ConnectionMode
import com.boswelja.watchconnection.core.BaseClient
import com.boswelja.watchconnection.core.Platform
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
    public suspend fun getCapabilitiesFor(watch: Watch): Set<String>? {
        return platforms[watch.platform]?.getCapabilitiesFor(watch.internalId)
    }

    /**
     * Check whether the given watch has a specified capability.
     * @param watch The [Watch] to check against.
     * @param capability The capability to look for.
     * @return a [Flow] of [Boolean], where true indicates the watch has the capability.
     */
    public fun hasCapability(watch: Watch, capability: String): Flow<Boolean> {
        val platform = platforms[watch.platform]
        checkNotNull(platform) { "No platform found for watch $watch" }
        return platform.watchHasCapability(watch.internalId, capability)
    }

    /**
     * Get a [Flow] of [Watch]es with a given capability.
     * @param capability The target capability.
     * @return A [Flow] of [Watch]es that declare [capability].
     */
    public fun watchesWithCapability(capability: String): Flow<List<Watch>> =
        combine(platforms.values.map { it.watchesWithCapability(capability) }) { flows ->
            val list = mutableListOf<Watch>()
            flows.forEach { watches ->
                list += watches
            }
            list
        }

    /**
     * Gets a [Flow] of [ConnectionMode] for the given watch. Use this to observe the connection
     * mode of a watch.
     * @param watch The [Watch] whose connection mode to observe.
     */
    public fun connectionModeFor(watch: Watch): Flow<ConnectionMode> {
        val platform = platforms[watch.platform]
        checkNotNull(platform) { "No platform found for watch $watch" }
        return platform.connectionModeFor(watch.internalId)
    }

    /**
     * Declare this device has a given capability.
     * @param capability The capability to declare.
     * @return true if the local capabilities were changed, false otherwise.
     */
    public suspend fun addLocalCapability(capability: String): Boolean {
        val results = platforms.values.map { it.addLocalCapability(capability) }
        return results.any { it }
    }

    /**
     * Declare this device no longer has a given capability.
     * @param capability The capability to remove.
     * @return true if the local capabilities were changed, false otherwise.
     */
    public suspend fun removeLocalCapability(capability: String): Boolean {
        val results = platforms.values.map { it.removeLocalCapability(capability) }
        return results.any { it }
    }
}
