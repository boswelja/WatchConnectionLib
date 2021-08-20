package com.boswelja.watchconnection.core.message

import android.content.Context

class ConcreteMessageReceiver : MessageReceiver() {

    val receivedMessages = mutableListOf<ReceivedMessage<ByteArray?>>()

    override suspend fun onMessageReceived(context: Context, message: ReceivedMessage<ByteArray?>) {
        receivedMessages.add(message)
    }
}
