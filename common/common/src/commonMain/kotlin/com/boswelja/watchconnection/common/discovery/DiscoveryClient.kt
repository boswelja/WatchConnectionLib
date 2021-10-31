package com.boswelja.watchconnection.common.discovery

import kotlinx.coroutines.flow.Flow

public interface DiscoveryClient {

    /**
     * Get a flow of capabilities found for a device with a given UID.
     * @param targetUid The device UID to get capabilities for.
     * @return A [Flow] of capability strings declared by the watch.
     */
    public suspend fun getCapabilitiesFor(targetUid: String): Set<String>

    /**
     * Check whether the given watch has a specified capability.
     * @param targetUid The device UID to check against.
     * @param capability The capability to look for.
     * @return a [Flow] of [Boolean], where true indicates the watch has the capability.
     */
    public fun hasCapability(targetUid: String, capability: String): Flow<Boolean>

    /**
     * Gets a [Flow] of [ConnectionMode] for the given watch. Use this to observe the connection
     * mode of a watch.
     * @param targetUid The device UID whose connection mode to observe.
     */
    public fun connectionModeFor(targetUid: String): Flow<ConnectionMode>

    /**
     * Declare this device has a given capability.
     * @param capability The capability to declare.
     * @return true if the local capabilities were changed, false otherwise.
     */
    public suspend fun addLocalCapability(capability: String): Boolean

    /**
     * Declare this device no longer has a given capability.
     * @param capability The capability to remove.
     * @return true if the local capabilities were changed, false otherwise.
     */
    public suspend fun removeLocalCapability(capability: String): Boolean
}
