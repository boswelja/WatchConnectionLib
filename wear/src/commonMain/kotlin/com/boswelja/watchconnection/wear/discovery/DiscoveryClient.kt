package com.boswelja.watchconnection.wear.discovery

import com.boswelja.watchconnection.wear.Phone
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
     * Add a capability to this smartwatch.
     */
    public suspend fun addCapability(capability: String)

    /**
     * Remove a capability from this smartwatch.
     */
    public suspend fun removeCapability(capability: String)

    /**
     * Get a set of capabilities declared by the paired [Phone]. The returned [Flow] will continue
     * emitting changes as long as there's an active collector.
     */
    public fun phoneCapabilities(): Flow<Set<String>>

    /**
     * Get whether the paired [Phone] is currently connected. The returned [Flow] will continue
     * emitting changes as long as there's an active collector.
     */
    public fun phoneConnected(): Flow<Boolean>
}