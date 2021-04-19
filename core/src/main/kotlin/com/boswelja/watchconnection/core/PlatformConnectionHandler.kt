package com.boswelja.watchconnection.core

import kotlinx.coroutines.flow.Flow

/**
 * The base connection handler to be implemented by supported platforms.
 */
interface PlatformConnectionHandler {

    /**
     * Returns a unique string to identify this platform. This will be used to map watches to the
     * correct platform as needed.
     */
    val platformIdentifier: String

    /**
     * A flow of all available watches for this platform.
     */
    fun allWatches(): Flow<Watch>

    /**
     * A flow of all available watches with the companion app installed for this platform.
     */
    fun watchesWithApp(): Flow<Watch>

    /**
     * Get aa array of capabilities found for a [Watch] with a given ID.
     * @param watchId See [Watch.platformId].
     * @return A [Flow] of capability strings declared by the watch.
     */
    suspend fun getCapabilitiesFor(watchId: String): Flow<String>

    /**
     * Send a message to a watch with the given ID.
     * @param watchId See [Watch.platformId].
     * @param message The message to send.
     * @param data The data to send with the message, if any.
     */
    suspend fun sendMessage(
        watchId: String,
        message: String,
        data: ByteArray? = null
    ): Result

    /**
     * Register a new [MessageListener].
     * @param listener The [MessageListener] to register.
     */
    fun registerMessageListener(listener: MessageListener)

    /**
     * Unregister a [MessageListener]. This will do nothing if the provided listener is not
     * registered.
     * @param listener The [MessageListener] to unregister.
     */
    fun unregisterMessageListener(listener: MessageListener)
}
