package com.boswelja.watchconnection.wear.discovery

import com.boswelja.watchconnection.common.Phone
import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.common.discovery.ConnectionMode
import com.boswelja.watchconnection.common.discovery.DiscoveryClient
import kotlinx.coroutines.flow.Flow

/**
 * A Discovery Client interface tailored to Wearables.
 */
public interface WearDiscoveryClient : DiscoveryClient {

    /**
     * Get the currently paired [Phone].
     */
    public suspend fun pairedPhone(): Phone?

    /**
     * Get the [Watch] representing this watch.
     */
    public suspend fun localWatch(): Watch

    /**
     * Get a set of capabilities declared by the paired phone.
     * @return The [Set] of capability strings declared by the paired phone.
     */
    public suspend fun allPhoneCapabilities(): Set<String>

    /**
     * Flow whether the paired phone has a given capability.
     * @param capability The capability to check for
     * @return A [Flow] of [Boolean] indicating whether the paired phone has the given capability.
     */
    public suspend fun phoneHasCapability(capability: String): Flow<Boolean>

    /**
     * Get a [Flow] that emits the current [ConnectionMode] between this watch and the paired
     * [Phone].
     */
    public fun connectionMode(): Flow<ConnectionMode>
}
