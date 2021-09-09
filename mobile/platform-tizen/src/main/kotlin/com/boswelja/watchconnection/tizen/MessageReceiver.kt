package com.boswelja.watchconnection.tizen

abstract class MessageReceiver {
    abstract fun onMessageReceived(watchId: String, message: String, data: ByteArray?)
}
