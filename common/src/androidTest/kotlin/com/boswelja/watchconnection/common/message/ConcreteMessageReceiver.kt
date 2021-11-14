package com.boswelja.watchconnection.common.message

import android.content.Context

public class ConcreteMessageReceiver : MessageReceiver() {

    public var receivedMessage: ReceivedMessage<ByteArray?>? = null

    override suspend fun onMessageReceived(
        context: Context,
        message: ReceivedMessage<ByteArray?>
    ) {
        receivedMessage = message
    }
}
