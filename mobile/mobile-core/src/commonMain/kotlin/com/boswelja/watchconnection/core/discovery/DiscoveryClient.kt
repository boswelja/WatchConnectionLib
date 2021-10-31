package com.boswelja.watchconnection.core.discovery

import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.common.discovery.ConnectionMode
import com.boswelja.watchconnection.common.discovery.DiscoveryClient
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
) : BaseClient<DiscoveryPlatform>(platforms), DiscoveryClient {

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
     * @param targetUid See [Watch.uid].
     * @return A [Flow] of capability strings declared by the watch.
     */
    override suspend fun getCapabilitiesFor(targetUid: String): Set<String> {
        val (platformId, internalId) = Watch.getInfoFromUid(targetUid)
        return getCapabilitiesFor(platformId, internalId)
    }

    /**
     * Get a flow of capabilities found for a given [Watch].
     * @param watch See [Watch].
     * @return A [Flow] of capability strings declared by the watch.
     */
    public suspend fun getCapabilitiesFor(watch: Watch): Set<String> {
        return getCapabilitiesFor(watch.platform, watch.internalId)
    }

    /**
     * Get a flow of capabilities found for a given [Watch].
     * @param platformId The platform identifier of the corresponding platform.
     * @param internalId The [Watch.internalId] of the device to send the message to.
     * @return A [Flow] of capability strings declared by the watch.
     */
    internal suspend fun getCapabilitiesFor(
        platformId: String,
        internalId: String
    ): Set<String> {
        val platform = platforms[platformId]
        checkNotNull(platform) { "No platform found for $platformId" }
        return platform.getCapabilitiesFor(internalId)
    }

    /**
     * Check whether the given watch has a specified capability.
     * @param targetUid The [Watch.uid] to check against.
     * @param capability The capability to look for.
     * @return a [Flow] of [Boolean], where true indicates the watch has the capability.
     */
    override fun hasCapability(targetUid: String, capability: String): Flow<Boolean> {
        val (platformId, internalId) = Watch.getInfoFromUid(targetUid)
        return hasCapability(platformId, internalId, capability)
    }

    /**
     * Check whether the given watch has a specified capability.
     * @param watch The [Watch] to check against.
     * @param capability The capability to look for.
     * @return a [Flow] of [Boolean], where true indicates the watch has the capability.
     */
    public fun hasCapability(watch: Watch, capability: String): Flow<Boolean> {
        return hasCapability(watch.platform, watch.internalId, capability)
    }

    /**
     * Check whether a device on the given platform has a specified capability.
     * @param platformId The platform identifier of the corresponding platform.
     * @param internalId The [Watch.internalId] of the device to send the message to.
     * @param capability The capability to look for.
     * @return A [Flow] of [Boolean], where true indicates the device has the capability.
     */
    internal fun hasCapability(
        platformId: String,
        internalId: String,
        capability: String
    ): Flow<Boolean> {
        val platform = platforms[platformId]
        checkNotNull(platform) { "No platform found for $platformId" }
        return platform.watchHasCapability(internalId, capability)
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
        return connectionModeFor(watch.platform, watch.internalId)
    }

    /**
     * Gets a [Flow] of [ConnectionMode] for the given watch. Use this to observe the connection
     * mode of a watch.
     * @param targetUid The [Watch.uid] whose connection mode to observe.
     */
    override fun connectionModeFor(targetUid: String): Flow<ConnectionMode> {
        val (platformId, internalId) = Watch.getInfoFromUid(targetUid)
        return connectionModeFor(platformId, internalId)
    }

    /**
     * Gets a [Flow] of [ConnectionMode] for the given watch. Use this to observe the connection
     * mode of a watch.
     * @param platformId The platform identifier of the corresponding platform.
     * @param internalId The [Watch.internalId] of the device to send the message to.
     */
    internal fun connectionModeFor(
        platformId: String,
        internalId: String
    ): Flow<ConnectionMode> {
        val platform = platforms[platformId]
        checkNotNull(platform) { "No platform found for $platformId" }
        return platform.connectionModeFor(internalId)
    }

    /**
     * Declare this device has a given capability.
     * @param capability The capability to declare.
     * @return true if the local capabilities were changed, false otherwise.
     */
    override suspend fun addLocalCapability(capability: String): Boolean {
        val results = platforms.values.map { it.addLocalCapability(capability) }
        return results.any { it }
    }

    /**
     * Declare this device no longer has a given capability.
     * @param capability The capability to remove.
     * @return true if the local capabilities were changed, false otherwise.
     */
    override suspend fun removeLocalCapability(capability: String): Boolean {
        val results = platforms.values.map { it.removeLocalCapability(capability) }
        return results.any { it }
    }
}
