package com.boswelja.watchconnection.core.discovery

import com.boswelja.watchconnection.common.discovery.Status
import com.boswelja.watchconnection.core.Phone
import kotlinx.coroutines.flow.Flow

/**
 * The base class for managing 'Discovery'. This class covers managing local capabilities, as well
 * as getting the paired phone and related details.
 */
abstract class BaseDiscoveryClient {

    /**
     * Gets the [Phone] paired to this watch.
     */
    abstract suspend fun pairedPhone(): Phone

    /**
     * Add a capability to this watch.
     * @param capability The capability to add.
     */
    abstract suspend fun addCapability(capability: String)

    /**
     * Remove a capability from this watch.
     * @param capability The capability to remove.
     */
    abstract suspend fun removeCapability(capability: String)

    /**
     * Flows the paired [Phone]'s capabilities.
     */
    abstract fun phoneCapabilities(): Flow<List<String>>

    /**
     * Flows the paired [Phone]'s [Status].
     */
    abstract fun phoneStatus(): Flow<Status>
}
