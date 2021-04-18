package com.boswelja.watchconnection.core

import android.content.Context

/**
 * The base connection handler to be implemented by supported platforms.
 */
abstract class PlatformConnectionHandler(context: Context) {
    
    /**
     * A list of all available watches for this platform.
     */
    abstract val availableWatches: List<Watch>

    /**
     * Returns a unique string to identify this platform. This will be used to map watches to the
     * correct platform as needed.
     */
    abstract val platformIdentifier: String

    /**
     * Send a message to a watch with the given ID.
     * @param watchId See [Watch.platformId].
     * @param message The message to send.
     * @param data The data to send with the message, if any.
     */
    abstract suspend fun sendMessage(
        watchId: String,
        message: String,
        data: ByteArray? = null
    ): Result

    /**
     * Register a new [MessageListener].
     * @param listener The [MessageListener] to register.
     */
    abstract suspend fun registerMessageListener(listener: MessageListener)

    /**
     * Unregister a [MessageListener]. This will do nothing if the provided listener is not
     * registered.
     * @param listener The [MessageListener] to unregister.
     */
    abstract suspend fun unregisterMessageListener(listener: MessageListener)

    /**
     * Manually refresh info such as watch status and available watches.
     */
    abstract suspend fun refreshData(): Result
}
