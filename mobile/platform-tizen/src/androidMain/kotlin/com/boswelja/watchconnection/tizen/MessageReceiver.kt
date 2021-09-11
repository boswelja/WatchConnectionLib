package com.boswelja.watchconnection.tizen

public abstract class MessageReceiver {
    public abstract fun onMessageReceived(watchId: String, message: String, data: ByteArray?)
}
