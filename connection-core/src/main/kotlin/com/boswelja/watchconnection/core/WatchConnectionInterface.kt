package com.boswelja.watchconnection.core

interface WatchConnectionInterface {
    
    /**
     * An observable list of all available watches.
     */
    val availableWatches: List<Watch>

    /**
     * Returns a string unique to the platform handling the connections. It's important this is
     * constant as it will be stored with registered watches.
     */
    val platformIdentifier: String

    /**
     * Send a message to a watch with the given ID.
     * @param watchId See [Watch.id].
     * @param path The message path to send.
     * @param data The data to send with the message, if any.
     */
    suspend fun sendMessage(watchId: String, path: String, data: ByteArray? = null)

    /**
     * Manually refresh info such as watch status and available watches.
     */
    suspend fun refreshData()
}
