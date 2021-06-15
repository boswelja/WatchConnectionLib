package com.boswelja.watchconnection.core.message

import com.boswelja.watchconnection.core.Platform
import com.boswelja.watchconnection.core.Watch
import kotlinx.coroutines.flow.Flow

/**
 * MessageClient provides an interface to access messages for a given platform.
 */
interface MessagePlatform : Platform {

    /**
     * A [Flow] of [Message]s received by this platform. This should not emit anything unless there
     * are collectors attached.
     */
    fun incomingMessages(): Flow<Message>

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
        priority: Message.Priority = Message.Priority.LOW
    ): Boolean
}
