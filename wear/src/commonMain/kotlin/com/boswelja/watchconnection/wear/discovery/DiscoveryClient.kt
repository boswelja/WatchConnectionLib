package com.boswelja.watchconnection.wear.discovery

import com.boswelja.watchconnection.common.Phone
import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.common.discovery.ConnectionMode
import kotlinx.coroutines.flow.Flow

/**
 * A client to manage local watch capabilities, as well as paired phone capabilities and status.
 */
public expect class DiscoveryClient {

    /**
     * Get the currently paired [Phone].
     */
    public suspend fun pairedPhone(): Phone

    /**
     * Get the [Watch] representing this watch.
     */
    public suspend fun localWatch(): Watch

    /**
     * Add a capability to this smartwatch.
     * @param capability The capability to add.
     * @return true if the local capability list was changed, false otherwise
     */
    public suspend fun addLocalCapability(capability: String): Boolean

    /**
     * Remove a capability from this smartwatch.
     * @param capability The capability to remove.
     * @return true if the local capability list was changed, false otherwise
     */
    public suspend fun removeLocalCapability(capability: String): Boolean

    /**
     * Get a set of capabilities declared by the paired [Phone]. The returned [Flow] will continue
     * emitting changes as long as there's an active collector.
     */
    public fun phoneCapabilities(): Flow<Set<String>>

    /**
     * Get a [Flow] that emits the current [ConnectionMode] between this watch and the paired
     * [Phone].
     */
    public fun connectionMode(): Flow<ConnectionMode>
}
