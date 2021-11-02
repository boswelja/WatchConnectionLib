package com.boswelja.watchconnection.wear.message

import com.boswelja.watchconnection.common.Phone
import com.boswelja.watchconnection.common.message.Message
import com.boswelja.watchconnection.common.message.MessageClient
import com.boswelja.watchconnection.common.message.MessageSerializer
import com.boswelja.watchconnection.common.message.ReceivedMessage
import kotlinx.coroutines.flow.Flow

/**
 * A client for sending and receiving messages.
 */
public expect class MessageClient : MessageClient {

    /**
     * Flows messages sent to this device, deserialized by a given [MessageSerializer]. Note only
     * messages supported by the given [MessageSerializer] will be emitted.
     * @param serializer The [MessageSerializer] to use.
     */
    @Deprecated("Use MessageHandler instead")
    public fun <T> incomingMessages(serializer: MessageSerializer<T>): Flow<ReceivedMessage<T>>

    /**
     * Sends a [Message] to the paired phone.
     * @return true if the message was sent, false otherwise. Note a successful result doesn't
     * guarantee delivery.
     */
    @Deprecated("Use MessageHandler instead")
    public suspend fun <T> sendMessage(targetPhone: Phone, message: Message<T>): Boolean
}
