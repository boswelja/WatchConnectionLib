package com.boswelja.watchconnection.core

import java.util.UUID

class ConcreteMessageReceiver : MessageReceiver() {

    override suspend fun onMessageReceived(
        sourceWatchId: UUID,
        message: String,
        data: ByteArray?
    ) { }
}
