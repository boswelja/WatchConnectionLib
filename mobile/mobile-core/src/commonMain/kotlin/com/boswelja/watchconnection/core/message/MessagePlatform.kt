package com.boswelja.watchconnection.core.message

import com.boswelja.watchconnection.common.message.Message
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.boswelja.watchconnection.core.Platform
import kotlinx.coroutines.flow.Flow

/**
 * [Platform] support for sending and receiving messages.
 */
public abstract class MessagePlatform : Platform {

    /**
     * A [Flow] of [ReceivedMessage]s received by this platform.
     */
    public abstract fun incomingMessages(): Flow<ReceivedMessage<ByteArray?>>

    /**
     * Send a message to a watch with the given ID. Note that a successful response doesn't
     * guarantee delivery.
     * @param watchId See [com.boswelja.watchconnection.common.Watch.internalId].
     * @param message The message to send.
     * @param data The data to send with the message, if any.
     * @param priority The message priority.
     * @return true if sending was successful, false otherwise.
     */
    public abstract suspend fun sendMessage(
        watchId: String,
        message: String,
        data: ByteArray? = null,
        priority: Message.Priority
    ): Boolean
}
