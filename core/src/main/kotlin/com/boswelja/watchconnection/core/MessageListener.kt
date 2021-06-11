package com.boswelja.watchconnection.core

import java.util.UUID

/**
 * An interface for message listeners. Add listener with [WatchPlatformManager.addMessageListener],
 * and remove with [WatchPlatformManager.removeMessageListener] when no longer needed.
 */
@Deprecated("Collect inbound messages with inboundMessages flow instead")
interface MessageListener {
    /**
     * Called when a message is received by this callback.
     * @param sourceWatchId The [UUID] of the source watch.
     * @param message The message that was received.
     * @param data The data sent with the message, or null if there was none.
     */
    fun onMessageReceived(sourceWatchId: UUID, message: String, data: ByteArray?)
}
