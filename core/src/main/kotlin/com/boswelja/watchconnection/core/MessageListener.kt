package com.boswelja.watchconnection.core

import java.util.UUID

interface MessageListener {

    /**
     * Called when a message is received by this callback.
     * @param sourceWatchId The [UUID] of the source watch.
     * @param message The message that was received.
     * @param data The data sent with the message, or null if there was none.
     */
    fun onMessageReceived(sourceWatchId: UUID, message: String, data: ByteArray?)
}