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
    fun allWatches(): Flow<Watch> =connectionHandlers.values.map { it.allWatches() }.merge()

    /**
     * Get a [Flow] of all [Watch]es determined to have the companion app installed from all
     * [PlatformConnectionHandler]s.
     */
    @ExperimentalCoroutinesApi
    fun watchesWithApp(): Flow<Watch> =
        connectionHandlers.values.map { it.watchesWithApp() }.merge()

    /**
     * Send a message to a specified [Watch].
     * @param to The [Watch]es to send the message to.
     * @param message The message to send.
     * @param data The data to send with the message, if any.
     */
    suspend fun sendMessage(vararg to: Watch, message: String, data: ByteArray? = null) {
        to.forEach { watch ->
            connectionHandlers[watch.platform]?.sendMessage(watch.platformId, message, data)
        }
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