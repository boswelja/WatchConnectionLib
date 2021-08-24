package com.boswelja.watchconnection.common.message.serialized

import android.content.Context
import com.boswelja.watchconnection.common.message.ReceivedMessage

class ConcreteMessageReceiver : TypedMessageReceiver<ConcreteDataType>(
    serializer = ConcreteMessageSerializer
) {
    val receivedMessages = mutableListOf<ReceivedMessage<ConcreteDataType>>()

    override suspend fun onTypedMessageReceived(
        context: Context,
        message: ReceivedMessage<ConcreteDataType>
    ) {
        receivedMessages.add(message)
    }
}
