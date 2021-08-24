package com.boswelja.watchconnection.common.message

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A [BroadcastReceiver] for receiving messages from watches of all supported platforms.
 */
abstract class MessageReceiver : BroadcastReceiver() {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    /**
     * Called when a message has been received. While this is a suspendable function, the limits
     * of [BroadcastReceiver.goAsync] still apply.
     * @param message The [ReceivedMessage] that was received.
     */
    abstract suspend fun onMessageReceived(
        context: Context,
        message: ReceivedMessage<ByteArray?>
    )

    final override fun onReceive(context: Context?, intent: Intent?) {
        // Don't handle intent if it's not ACTION_MESSAGE_RECEIVED
        if (intent?.action != Messages.ACTION_MESSAGE_RECEIVED) return

        context?.let {
            // Going async
            val pendingResult = goAsync()
            coroutineScope.launch {
                // Collect data from intent
                val watchId = UUID.fromString(intent.getStringExtra(EXTRA_WATCH_ID))
                val message = intent.getStringExtra(EXTRA_MESSAGE)!!
                val data = intent.getByteArrayExtra(EXTRA_DATA)

                // Pass it on to user code
                onMessageReceived(
                    context,
                    ReceivedMessage(watchId, message, data)
                )

                // Let the BroadcastReceiver know we're done
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val EXTRA_WATCH_ID = "watch_id"
        const val EXTRA_MESSAGE = "message"
        const val EXTRA_DATA = "data"
    }
}
