package com.boswelja.watchconnection.core.discovery

import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.common.discovery.ConnectionMode
import com.boswelja.watchconnection.common.discovery.Status
import com.boswelja.watchconnection.core.Platform
import kotlinx.coroutines.flow.Flow

/**
 * [Platform] support for discovering watches, watch capabilities, and watch status.
 */
public abstract class DiscoveryPlatform : Platform {

    /**
     * A flow of all available watches for this platform.
     */
    public abstract fun allWatches(): Flow<List<Watch>>

    /**
     * A flow of all available watches with the companion app installed for this platform.
     */
    public abstract fun watchesWithApp(): Flow<List<Watch>>

    /**
     * Get a flow of capabilities found for a [Watch] with a given ID.
     * @param watchId See [Watch.internalId].
     * @return A [Flow] of capability strings declared by the watch.
     */
    public abstract fun getCapabilitiesFor(watchId: String): Flow<List<String>>

    /**
     * Gets a [Flow] of [Status] for the watch with the given ID.
     * @param watchId See [Watch.internalId].
     * @return The [Flow] of [Status].
     */
    public abstract fun getStatusFor(watchId: String): Flow<Status>

    /**
     * Gets a [Flow] of [ConnectionMode] for the given watch. Use this to observe the connection
     * mode of a watch.
     * @param watch The [Watch] whose connection mode to observe.
     */
    public abstract fun connectionModeFor(watch: Watch): Flow<ConnectionMode>
}
