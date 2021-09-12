package com.boswelja.watchconnection.wear.message

import com.boswelja.watchconnection.common.message.Message
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.boswelja.watchconnection.common.message.serialized.MessageSerializer
import com.boswelja.watchconnection.wear.Phone
import kotlinx.coroutines.flow.Flow

/**
 * A client for sending and receiving messages.
 */
public expect class MessageClient {

    /**
     * Flows messages sent to this device, deserialized by a given [MessageSerializer]. Note only
     * messages supported by the given [MessageSerializer] will be emitted.
     * @param serializer The [MessageSerializer] to use.
     */
    public fun <T> incomingMessages(serializer: MessageSerializer<T>): Flow<ReceivedMessage<T>>

    /**
     * Sends a [Message] to the paired phone.
     * @return true if the message was sent, false otherwise. Note a successful result doesn't
     * guarantee delivery.
     */
    public suspend fun <T> sendMessage(targetPhone: Phone, message: Message<T>): Boolean
}