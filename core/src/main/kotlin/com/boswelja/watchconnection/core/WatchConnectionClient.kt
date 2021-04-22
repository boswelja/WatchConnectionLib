package com.boswelja.watchconnection.core

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge

/**
 * A class to simplify handling multiple [PlatformConnectionHandler].
 * @param connectionHandler All [PlatformConnectionHandler]s to manage.
 */
class WatchConnectionClient(
    vararg connectionHandler: PlatformConnectionHandler
) {

    /** A map of platform IDs to their handlers */
    private val connectionHandlers = HashMap<String, PlatformConnectionHandler>()

    init {
        // Map platform IDs to their handlers for easier access later
        connectionHandler.forEach {
            connectionHandlers[it.platformIdentifier] = it
        }
    }

    /**
     * Get a [Flow] of all [Watch]es found by all [PlatformConnectionHandler]s.
     */
    @ExperimentalCoroutinesApi
    fun allWatches(): Flow<Watch> = connectionHandlers.values.map { it.allWatches() }.merge()

    /**
     * Get a [Flow] of all [Watch]es determined to have the companion app installed from all
     * [PlatformConnectionHandler]s.
     */
    @ExperimentalCoroutinesApi
    fun watchesWithApp(): Flow<Watch> =
        connectionHandlers.values.map { it.watchesWithApp() }.merge()

    /**
     * Send a message to a [Watch].
     * @param to The [Watch] to send the message to.
     * @param message The message to send.
     * @param data The data to send with the message, if any.
     * @return true if sending the message was successful, false otherwise.
     */
    suspend fun sendMessage(to: Watch, message: String, data: ByteArray? = null) =
        connectionHandlers[to.platform]?.sendMessage(to.platformId, message, data) == true

    /**
     * Get a flow of capabilities found for a given [Watch].
     * @param watch See [Watch].
     * @return A [Flow] of capability strings declared by the watch.
     */
    fun getCapabilitiesFor(watch: Watch): Flow<String>? {
        return connectionHandlers[watch.platform]?.getCapabilitiesFor(watch.platformId)
    }

    /**
     * Registers a [MessageListener] on all platforms.
     */
    fun registerMessageListener(messageListener: MessageListener) {
        connectionHandlers.values.forEach {
            it.registerMessageListener(messageListener)
        }
    }

    /**
     * Unregisters a [MessageListener] on all platforms.
     */
    fun unregisterMessageListener(messageListener: MessageListener) {
        connectionHandlers.values.forEach {
            it.unregisterMessageListener(messageListener)
        }
    }
}
