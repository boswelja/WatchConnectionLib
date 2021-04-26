package com.boswelja.watchconnection.core

import android.content.Context
import java.util.UUID

class ConcreteMessageReceiver : MessageReceiver() {

    override suspend fun onMessageReceived(
        context: Context,
        sourceWatchId: UUID,
        message: String,
        data: ByteArray?
    ) { }
}
