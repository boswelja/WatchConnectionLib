package com.boswelja.watchconnection.core.message

import com.boswelja.watchconnection.common.message.Message
import com.boswelja.watchconnection.common.message.MessagePriority
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.boswelja.watchconnection.common.message.serialized.MessageSerializer
import com.boswelja.watchconnection.core.Phone
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

/**
 * The base class for managing 'Messaging'. This class manages sending and receiving messages,
 * including automatic serialization where possible.
 */
public abstract class BaseMessageClient(
    private val serializers: List<MessageSerializer<*>> = listOf()
) {

    /**
     * A [Flow] of [ReceivedMessage]s received by this device.
     */
    public abstract fun rawIncomingMessages(): Flow<ReceivedMessage<ByteArray?>>

    /**
     * Send a message to a target with the given ID. Note that a successful response doesn't
     * guarantee delivery. If possible, use [sendMessage] instead for serialization support.
     * @param message The message to send.
     * @return true if sending was successful, false otherwise.
     */
    public abstract suspend fun sendRawMessage(
        message: Message<ByteArray?>,
        priority: MessagePriority = MessagePriority.LOW
    ): Boolean

    /**
     * A [Flow] of [ReceivedMessage]s. Messages collected here will be deserialized automatically by
     * the [MessageSerializer]s you passed in when constructing this [BaseMessageClient] where
     * possible.
     */
    public fun incomingMessages(): Flow<ReceivedMessage<*>> = rawIncomingMessages()
        .map { message ->
            // Deserialize if possible
            val serializer = serializers.firstOrNull { it.messagePaths.contains(message.path) }
            if (serializer != null) {
                val data = message.data
                requireNotNull(data) { "Expected data with message $message" }

                val deserializedData = serializer.deserialize(data)
                ReceivedMessage(
                    message.sourceUid,
                    message.path,
                    deserializedData
                )
            } else {
                message
            }
        }

    /**
     * A [Flow] of [ReceivedMessage]s. Messages collected here will only ever be messages that
     * [serializer] can deserialize, thus guaranteeing the data type [T].
     * @param serializer The [MessageSerializer] to use for deserializing.
     */
    public fun <T> incomingMessages(
        serializer: MessageSerializer<T>
    ): Flow<ReceivedMessage<T>> = rawIncomingMessages()
        .mapNotNull { message ->
            if (serializer.messagePaths.contains(message.path)) {
                val data = message.data
                requireNotNull(data) { "Expected data with message $message" }

                val deserializedData = serializer.deserialize(data)
                ReceivedMessage(
                    message.sourceUid,
                    message.path,
                    deserializedData
                )
            } else null
        }

    /**
     * Send a message to a [Phone].
     * @param message The [Message] to send.
     * @return true if sending the message was successful, false otherwise.
     */
    public suspend fun sendMessage(
        message: Message<Any?>,
        priority: MessagePriority = MessagePriority.LOW
    ): Boolean {
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

        return sendRawMessage(Message(message.path, bytes), priority)
    }
}
