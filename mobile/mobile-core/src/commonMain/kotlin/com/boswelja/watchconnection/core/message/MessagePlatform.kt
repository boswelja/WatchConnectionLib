package com.boswelja.watchconnection.core.message

import com.boswelja.watchconnection.common.message.MessagePriority
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.boswelja.watchconnection.core.Platform
import kotlinx.coroutines.flow.Flow

/**
 * [Platform] support for sending and receiving messages.
 */
interface MessagePlatform : Platform {

    /**
     * A [Flow] of [ReceivedMessage]s received by this platform.
     */
    fun incomingMessages(): Flow<ReceivedMessage<ByteArray?>>

    /**
     * Send a message to a watch with the given ID. Note that a successful response doesn't
     * guarantee delivery.
     * @param watchId See [com.boswelja.watchconnection.core.Watch.internalId].
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
