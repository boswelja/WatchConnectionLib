package com.boswelja.watchconnection.core.message

import com.boswelja.watchconnection.core.BaseClient
import com.boswelja.watchconnection.core.Platform
import com.boswelja.watchconnection.core.Watch
import com.boswelja.watchconnection.core.message.serialized.DataSerializer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge

/**
 * MessageClient takes a number of [MessagePlatform]s, and provides a common interface between them.
 * @param serializers A list of [DataSerializer] to use when serializing/deserializing messages.
 * @param platforms The [MessagePlatform]s this MessageClient should support.
 */
class MessageClient(
    private val serializers: List<DataSerializer<*>> = listOf(),
    platforms: List<MessagePlatform>
) : BaseClient<MessagePlatform>(platforms) {

    /**
     * A [Flow] of [ReceivedMessage]s received by all platforms. Messages collected here have no
     * additional processing performed, and thus only contain the raw data in bytes.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun rawIncomingMessages(): Flow<ReceivedMessage<ByteArray?>> = platforms.values
        .map { it.incomingMessages() }
        .merge()

    /**
     * A [Flow] of [ReceivedMessage]s received by all [Platform]s. Messages collected here will be
     * deserialized automatically by the [DataSerializer]s you passed in when constructing this
     * [MessageClient] where possible.
     */
    fun incomingMessages(): Flow<ReceivedMessage<*>> = rawIncomingMessages()
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
     * A [Flow] of [ReceivedMessage]s from all platforms. Messages collected here will only ever be
     * messages that [serializer] can deserialize, thus guaranteeing the data type [T].
     * @param serializer The [DataSerializer] to use for deserializing.
     */
    fun <T> incomingMessages(
        serializer: DataSerializer<T>
    ): Flow<ReceivedMessage<T>> = rawIncomingMessages()
        .mapNotNull { message ->
            if (serializer.messagePaths.contains(message.path)) {
                requireNotNull(message.data) { "Expected data with message $message" }

                val deserializedData = serializer.deserialize(message.data)
                ReceivedMessage(
                    message.sourceWatchID,
                    message.path,
                    deserializedData
                )
            } else null
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

        val serializer = serializers.firstOrNull { it.messagePaths.contains(message.path) }
        val data = message.data
        val bytes = if (serializer != null) {
            requireNotNull(data) { "Expected data with message $message" }
            serializer.serializeAny(data)
        } else {
            require(data is ByteArray?) {
                "Invalid data $data. Did you forget to add a serializer?"
            }
            data
        }

        return platform.sendMessage(to.platformId, message.path, bytes, priority)
    }
}
