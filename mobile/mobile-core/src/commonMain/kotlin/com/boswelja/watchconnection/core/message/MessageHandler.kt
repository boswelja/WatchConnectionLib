package com.boswelja.watchconnection.core.message

import com.boswelja.watchconnection.common.message.Message
import com.boswelja.watchconnection.common.message.MessageSerializer
import com.boswelja.watchconnection.common.message.ReceivedMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

/**
 * A helper class designed to facilitate message transfer between devices for a single message type.
 * @param serializer The [MessageSerializer] this handler should use.
 * @param messageClient The base [MessageClient].
 */
public class MessageHandler<T>(
    private val serializer: MessageSerializer<T>,
    private val messageClient: com.boswelja.watchconnection.common.message.MessageClient
) {

    /**
     * Get incoming messages deserialized by [serializer]. Note there will be no messages with paths
     * not specified by the serializer.
     */
    public fun incomingMessages(): Flow<ReceivedMessage<T>> =
        messageClient.incomingMessages().mapNotNull { message ->
            if (serializer.messagePaths.contains(message.path)) {
                val deserializedData = serializer.deserialize(message.data)
                ReceivedMessage(
                    message.sourceUid,
                    message.path,
                    deserializedData
                )
            } else null
        }

    /**
     * Send a message to the device with the given UID. Note the message needs to have a path
     * specified in [serializer].
     * @param targetUid The target device UID.
     * @param message The message to send.
     */
    public suspend fun sendMessage(targetUid: String, message: Message<T>): Boolean {
        val serializedData = serializer.serialize(message.data)
        return messageClient.sendMessage(
            targetUid,
            Message(message.path, serializedData, message.priority)
        )
    }
}
