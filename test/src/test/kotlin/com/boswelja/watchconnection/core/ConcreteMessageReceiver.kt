package com.boswelja.watchconnection.core

import android.content.Context

class ConcreteMessageReceiver : MessageReceiver() {

    override suspend fun onMessageReceived(
        context: Context,
        message: Message
    ) { }
}
