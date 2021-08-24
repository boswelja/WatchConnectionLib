package com.boswelja.watchconnection.tizen

import java.util.UUID

abstract class MessageReceiver {
    abstract fun onMessageReceived(watchId: UUID, message: String, data: ByteArray?)
}
