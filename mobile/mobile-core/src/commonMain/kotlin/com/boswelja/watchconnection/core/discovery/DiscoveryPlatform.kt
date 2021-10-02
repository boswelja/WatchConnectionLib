package com.boswelja.watchconnection.core.discovery

import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.common.discovery.ConnectionMode
import com.boswelja.watchconnection.common.internal.discovery.Capabilities
import com.boswelja.watchconnection.core.Platform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * [Platform] support for discovering watches, watch capabilities, and watch status.
 */
public abstract class DiscoveryPlatform : Platform {

    init {
        CoroutineScope(Dispatchers.Default).launch {
            addLocalCapability(Capabilities.ConnectionLibHost)
        }
    }

    /**
     * A flow of all available watches for this platform.
     */
    public abstract fun allWatches(): Flow<List<Watch>>

    /**
     * Get a flow of capabilities found for a [Watch] with a given ID.
     * @param watchId See [Watch.internalId].
     * @return A [Flow] of capability strings declared by the watch.
     */
    public abstract suspend fun getCapabilitiesFor(watchId: String): Set<String>

    /**
     * Check whether the given watch has a specified capability.
     * @param watch The [Watch] to check against.
     * @param capability The capability to look for.
     * @return a [Flow] of [Boolean], where true indicates the watch has the capability.
     */
    public abstract fun watchHasCapability(watch: Watch, capability: String): Flow<Boolean>

    /**
     * Get a [Flow] of [Watch]es with a given capability.
     * @param capability The target capability.
     * @return A [Flow] of [Watch]es that declare [capability].
     */
    public abstract fun watchesWithCapability(capability: String): Flow<List<Watch>>

    /**
     * Gets a [Flow] of [ConnectionMode] for the given watch. Use this to observe the connection
     * mode of a watch.
     * @param watch The [Watch] whose connection mode to observe.
     */
    public abstract fun connectionModeFor(watch: Watch): Flow<ConnectionMode>

    /**
     * Declare this device has a given capability.
     * @param capability The capability to declare.
     * @return true if the local capabilities were changed, false otherwise.
     */
    public abstract suspend fun addLocalCapability(capability: String): Boolean

    /**
     * Declare this device no longer has a given capability.
     * @param capability The capability to remove.
     * @return true if the local capabilities were changed, false otherwise.
     */
    public abstract suspend fun removeLocalCapability(capability: String): Boolean
}
