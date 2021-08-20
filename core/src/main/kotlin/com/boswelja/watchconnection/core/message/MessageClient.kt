package com.boswelja.watchconnection.core.message

import com.boswelja.watchconnection.core.BaseClient
import com.boswelja.watchconnection.core.Platform
import com.boswelja.watchconnection.core.Watch
import com.boswelja.watchconnection.core.message.serialized.DataSerializer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

/**
 * MessageClient takes a number of [MessagePlatform]s, and provides a common interface between them.
 * @param platforms The [MessagePlatform]s this MessageClient should support.
 */
class MessageClient(
    private val serializers: List<DataSerializer<*>> = listOf(),
    platforms: List<MessagePlatform>
) : BaseClient<MessagePlatform>(platforms) {

    /**
     * A [Flow] of [ReceivedMessage]s received by all [Platform]s. See [MessagePlatform.incomingMessages].
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun incomingMessages(): Flow<ReceivedMessage<*>> = platforms.values
        .map { it.incomingMessages() }
        .merge()
        .map { message ->
            // Deserialize if possible
            val serializer = serializers.firstOrNull { it.messagePaths.contains(message.path) }
            if (serializer != null) {
                requireNotNull(message.data) { "Expected data with message $message" }

                val deserializedData = serializer.deserialize(message.data)
                ReceivedMessage(
                    message.sourceWatchID,
                    message.path,
                    deserializedData
                )
            } else {
                message
            }
        }

    /**
     * Send a message to a [Watch]. See [MessagePlatform.sendMessage].
     * @param to The [Watch] to send the message to.
     * @param message The [Message] to send.
     * @return true if sending the message was successful, false otherwise.
     */
    suspend fun sendMessage(
        to: Watch,
        message: Message<Any?>,
        priority: MessagePriority = MessagePriority.LOW
    ): Boolean {
        val platform = platforms[to.platform]
        requireNotNull(platform) { "No platform registered for watch $to" }

        val bytes = message.data?.let { data ->
            val serializer = serializers.firstOrNull { it.messagePaths.contains(message.path) }
            if (serializer != null) {
                serializer.serializeAny(data)
            } else {
                require(data is ByteArray) { "No serializer registered for ${data::class}" }
                data
            }
        }

        return platform.sendMessage(to.platformId, message.path, bytes, priority)
    }
}
