package com.boswelja.watchconnection.core.message

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow

class ConcreteMessageReceiver : MessageReceiver() {

    val receivedMessage = MutableStateFlow<Message?>(null)

    override suspend fun onMessageReceived(context: Context, message: Message) {
        receivedMessage.emit(message)
    }
}
