package com.boswelja.watchconnection.core

import kotlinx.coroutines.flow.Flow

/**
 * The base connection handler to be implemented by supported platforms.
 */
abstract class PlatformConnectionHandler {

    /**
     * Returns a unique string to identify this platform. This will be used to map watches to the
     * correct platform as needed.
     */
    protected abstract val platformIdentifier: String

    /**
     * A flow of all available watches for this platform.
     */
    abstract fun allWatches(): Flow<Watch>

    /**
     * A flow of all available watches with the companion app installed for this platform.
     */
    abstract fun watchesWithApp(): Flow<Watch>

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
}
