package com.boswelja.watchconnection.core.discovery

import com.boswelja.watchconnection.common.discovery.Status
import com.boswelja.watchconnection.core.Platform
import com.boswelja.watchconnection.core.Watch
import kotlinx.coroutines.flow.Flow

/**
 * [Platform] support for discovering watches, watch capabilities, and watch status.
 */
public interface DiscoveryPlatform : Platform {

    /**
     * A flow of all available watches for this platform.
     */
    public fun allWatches(): Flow<List<Watch>>

    /**
     * A flow of all available watches with the companion app installed for this platform.
     */
    public fun watchesWithApp(): Flow<List<Watch>>

    /**
     * Get a flow of capabilities found for a [Watch] with a given ID.
     * @param watchId See [Watch.internalId].
     * @return A [Flow] of capability strings declared by the watch.
     */
    public fun getCapabilitiesFor(watchId: String): Flow<List<String>>

    /**
     * Gets a [Flow] of [Status] for the watch with the given ID.
     * @param watchId See [Watch.internalId].
     * @return The [Flow] of [Status].
     */
    public fun getStatusFor(watchId: String): Flow<Status>
}
