package com.boswelja.watchconnection.core

import kotlinx.coroutines.flow.Flow

/**
 * The base connection handler to be implemented by supported platforms.
 */
interface WatchPlatform {

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
     * Get a flow of capabilities found for a [Watch] with a given ID.
     * @param watchId See [Watch.platformId].
     * @return A [Flow] of capability strings declared by the watch.
     */
    fun getCapabilitiesFor(watchId: String): Flow<String>

    /**
     * Send a message to a watch with the given ID. Note that a successful response doesn't
     * guarantee delivery.
     * @param watchId See [Watch.platformId].
     * @param message The message to send.
     * @param data The data to send with the message, if any.
     * @return true if sending was successful, false otherwise.
     */
    suspend fun sendMessage(
        watchId: String,
        message: String,
        data: ByteArray? = null
    ): Boolean

    /**
     * Adds a new [MessageListener].
     * @param listener The [MessageListener] to register.
     */
    fun addMessageListener(listener: MessageListener)

    /**
     * Removes a [MessageListener]. This will do nothing if the provided listener is not
     * registered.
     * @param listener The [MessageListener] to unregister.
     */
    fun removeMessageListener(listener: MessageListener)
}
