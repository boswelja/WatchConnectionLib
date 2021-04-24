package com.boswelja.watchconnection.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * A [BroadcastReceiver] for receiving messages from watches of all supported platforms.
 */
abstract class MessageReceiver : BroadcastReceiver() {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    /**
     * Called when a message has been received. While this is a suspendable function, the limits
     * of [BroadcastReceiver.goAsync] still apply.
     * @param sourceWatchId The [Watch.id] of the watch that sent the message.
     * @param message The message that was received.
     * @param data The data included with the message, or null if there was no data.
     */
    abstract suspend fun onMessageReceived(
        sourceWatchId: UUID,
        message: String,
        data: ByteArray?
    )

    final override fun onReceive(context: Context?, intent: Intent?) {
        // Don't handle intent if it's not ACTION_MESSAGE_RECEIVED
        if (intent?.action != Messages.ACTION_MESSAGE_RECEIVED) return

        // Going async
        val pendingResult = goAsync()
        coroutineScope.launch {
            // Collect data from intent
            val watchId = UUID.fromString(intent.getStringExtra(WATCH_ID_EXTRA))
            val message = intent.getStringExtra(MESSAGE_EXTRA)!!
            val data = intent.getByteArrayExtra(DATA_EXTRA)

            // Pass it on to user code
            onMessageReceived(watchId, message, data)

            // Let the BroadcastReceiver know we're done
            pendingResult.finish()
        }
    }

    companion object {
        const val WATCH_ID_EXTRA = "watch_id"
        const val MESSAGE_EXTRA = "message"
        const val DATA_EXTRA = "data"
    }
}
