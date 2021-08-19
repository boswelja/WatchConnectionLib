package com.boswelja.watchconnection.core.message

import com.boswelja.watchconnection.core.Platform
import com.boswelja.watchconnection.core.Watch
import kotlinx.coroutines.flow.Flow

/**
 * [Platform] support for sending and receiving messages.
 */
interface MessagePlatform : Platform {

    /**
     * A [Flow] of [ByteArrayMessage]s received by this platform.
     */
    fun incomingMessages(): Flow<ByteArrayMessage>

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
        data: ByteArray? = null,
        priority: MessagePriority = MessagePriority.LOW
    ): Boolean
}
