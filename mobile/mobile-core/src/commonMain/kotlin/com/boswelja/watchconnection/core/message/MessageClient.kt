package com.boswelja.watchconnection.core.message

import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.common.message.Message
import com.boswelja.watchconnection.common.message.MessageSerializer
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.boswelja.watchconnection.core.BaseClient
import com.boswelja.watchconnection.core.Platform
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge

/**
 * MessageClient takes a number of [MessagePlatform]s, and provides a common interface between them.
 * @param serializers A list of [MessageSerializer] to use when serializing/deserializing messages.
 * @param platforms The [MessagePlatform]s this MessageClient should support.
 */
public class MessageClient(
    private val serializers: List<MessageSerializer<*>> = listOf(),
    platforms: List<MessagePlatform>
) : BaseClient<MessagePlatform>(platforms) {

    /**
     * A [Flow] of [ReceivedMessage]s received by all platforms. Messages collected here have no
     * additional processing performed, and thus only contain the raw data in bytes.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    public fun rawIncomingMessages(): Flow<ReceivedMessage<ByteArray?>> = platforms.values
        .map { it.incomingMessages() }
        .merge()

    /**
     * A [Flow] of [ReceivedMessage]s received by all [Platform]s. Messages collected here will be
     * deserialized automatically by the [MessageSerializer]s you passed in when constructing this
     * [MessageClient] where possible.
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
     * A [Flow] of [ReceivedMessage]s from all platforms. Messages collected here will only ever be
     * messages that [serializer] can deserialize, thus guaranteeing the data type [T].
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
     * Send a message to a [Watch]. See [MessagePlatform.sendMessage].
     * @param targetUid The [Watch.uid] to send the message to.
     * @param message The [Message] to send.
     * @return true if sending the message was successful, false otherwise.
     */
    public suspend fun sendMessage(
        targetUid: String,
        message: Message<Any?>
    ): Boolean {
        val (platformId, internalId) = Watch.getInfoFromUid(targetUid)
        return sendMessage(platformId, internalId, message)
    }

    /**
     * Send a message to a [Watch]. See [MessagePlatform.sendMessage].
     * @param target The [Watch] to send the message to.
     * @param message The [Message] to send.
     * @return true if sending the message was successful, false otherwise.
     */
    public suspend fun sendMessage(
        target: Watch,
        message: Message<Any?>
    ): Boolean {
        return sendMessage(target.platform, target.internalId, message)
    }

    /**
     * Sends a given message to a device on the specified platform.
     * @param platformId The platform identifier of the corresponding platform.
     * @param internalId The [Watch.internalId] of the device to send the message to.
     * @param message The message to send.
     * @return true if the message was sent successfully. Note this does not guarantee successful
     * delivery.
     */
    internal suspend fun sendMessage(
        platformId: String,
        internalId: String,
        message: Message<Any?>
    ): Boolean {
        val platform = platforms[platformId]
        requireNotNull(platform) { "No platform registered for $platformId" }

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

        return platform.sendMessage(internalId, message.path, bytes, message.priority)
    }
}
