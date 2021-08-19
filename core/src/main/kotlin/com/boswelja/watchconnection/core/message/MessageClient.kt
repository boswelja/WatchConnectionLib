package com.boswelja.watchconnection.core.message

import com.boswelja.watchconnection.core.BaseClient
import com.boswelja.watchconnection.core.Platform
import com.boswelja.watchconnection.core.Watch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge

/**
 * MessageClient takes a number of [MessagePlatform]s, and provides a common interface between them.
 * @param platforms The [MessagePlatform]s this MessageClient should support.
 */
class MessageClient(
    vararg platforms: MessagePlatform
) : BaseClient<MessagePlatform>(*platforms) {

    /**
     * A [Flow] of [ByteArrayMessage]s received by all [Platform]s. See [MessagePlatform.incomingMessages].
     */
    @ExperimentalCoroutinesApi
    fun incomingMessages(): Flow<ByteArrayMessage> =
        platforms.values.map { it.incomingMessages() }.merge()

    /**
     * Send a message to a [Watch]. See [MessagePlatform.sendMessage].
     * @param to The [Watch] to send the message to.
     * @param message The message to send.
     * @param data The data to send with the message, if any.
     * @return true if sending the message was successful, false otherwise.
     */
    suspend fun sendMessage(
        to: Watch,
        message: String,
        data: ByteArray? = null,
        priority: MessagePriority = MessagePriority.LOW
    ) = platforms[to.platform]?.sendMessage(to.platformId, message, data, priority) == true
}
