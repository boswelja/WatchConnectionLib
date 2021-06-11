package com.boswelja.watchconnection.core

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.merge

/**
 * A class to simplify handling multiple [WatchPlatform].
 * @param platforms All [WatchPlatform]s to manage.
 */
class WatchPlatformManager(
    vararg platforms: WatchPlatform
) {

    /** A map of platform IDs to their handlers */
    private val connectionHandlers = HashMap<String, WatchPlatform>()

    init {
        // Map platform IDs to their handlers for easier access later
        platforms.forEach {
            connectionHandlers[it.platformIdentifier] = it
        }
    }

    /**
     * Get a [Flow] of all [Watch]es found by all [WatchPlatform]s.
     */
    @ExperimentalCoroutinesApi
    fun allWatches(): Flow<List<Watch>> =
        combine(connectionHandlers.values.map { it.allWatches() }) { flows ->
            val list = mutableListOf<Watch>()
            flows.forEach { watches ->
                list += watches
            }
            list
        }

    /**
     * Get a [Flow] of all [Watch]es determined to have the companion app installed from all
     * [WatchPlatform]s.
     */
    @ExperimentalCoroutinesApi
    fun watchesWithApp(): Flow<List<Watch>> =
        combine(connectionHandlers.values.map { it.watchesWithApp() }) { flows ->
            val list = mutableListOf<Watch>()
            flows.forEach { watches ->
                list += watches
            }
            list
        }

    /**
     * A [Flow] of [Message]s received by all [WatchPlatform]s.
     */
    @ExperimentalCoroutinesApi
    fun incomingMessages(): Flow<Message> =
        connectionHandlers.values.map { it.incomingMessages() }.merge()

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
    fun getCapabilitiesFor(watch: Watch): Flow<List<String>>? {
        return connectionHandlers[watch.platform]?.getCapabilitiesFor(watch.platformId)
    }

    /**
     * Gets a [Flow] of [Status] for a given [Watch].
     * @param watch The [Watch] to get a [Status] for.
     * @return The [Flow] of [Status]. May be null if the given watches platform doesn't exist in
     * this instance.
     */
    fun getStatusFor(watch: Watch): Flow<Status>? {
        return connectionHandlers[watch.platform]?.getStatusFor(watch.platformId)
    }
}
