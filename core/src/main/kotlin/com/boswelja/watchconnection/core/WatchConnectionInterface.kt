package com.boswelja.watchconnection.core

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
     * Manually refresh info such as watch status and available watches.
     */
    suspend fun refreshData()
}
