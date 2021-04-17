package com.boswelja.watchconnection.core

/**
 * The base connection interface to be implemented by all platforms.
 */
interface WatchConnectionInterface {
    
    /**
     * A list of all available watches for this platform.
     */
    val availableWatches: List<Watch>

    /**
     * Returns a unique string to identify this platform. This will be used to map watches to the
     * correct platform as needed.
     */
    val platformIdentifier: String

    /**
     * Send a message to a watch with the given ID.
     * @param watchId See [Watch.platformId].
     * @param message The message to send.
     * @param data The data to send with the message, if any.
     */
    suspend fun sendMessage(watchId: String, message: String, data: ByteArray? = null)

    /**
     * Register a new [MessageListener].
     * @param listener The [MessageListener] to register.
     */
    suspend fun registerMessageListener(listener: MessageListener)

    /**
     * Unregister a [MessageListener]. This will do nothing if the provided listener is not
     * registered.
     * @param listener The [MessageListener] to unregister.
     */
    suspend fun unregisterMessageListener(listener: MessageListener)

    /**
     * Manually refresh info such as watch status and available watches.
     */
    suspend fun refreshData()
}
