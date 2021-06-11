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
     * The [Flow] of [Message] received by this platform. This should not emit anything unless there
     * are collectors attached.
     */
    val incomingMessages: Flow<Message>

    /**
     * A flow of all available watches for this platform.
     */
    fun allWatches(): Flow<List<Watch>>

    /**
     * A flow of all available watches with the companion app installed for this platform.
     */
    fun watchesWithApp(): Flow<List<Watch>>

    /**
     * Get a flow of capabilities found for a [Watch] with a given ID.
     * @param watchId See [Watch.platformId].
     * @return A [Flow] of capability strings declared by the watch.
     */
    fun getCapabilitiesFor(watchId: String): Flow<List<String>>

    /**
     * Gets a [Flow] of [Status] for the watch with the given ID.
     * @param watchId See [Watch.platformId].
     * @return The [Flow] of [Status].
     */
    fun getStatusFor(watchId: String): Flow<Status>

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
}
