package com.boswelja.watchconnection.common.message

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
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
        if (intent?.action != ACTION_MESSAGE_RECEIVED) return

        context?.let {
            // Going async
            val pendingResult = goAsync()
            coroutineScope.launch {
                // Collect data from intent
                val watchId = intent.getStringExtra(EXTRA_WATCH_ID)!!
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

        /**
         * Sent when a message is received from any supported platform. Implement
         * [MessageReceiver] to receive this intent.
         */
        const val ACTION_MESSAGE_RECEIVED =
            "com.boswelja.watchconnection.messages.ACTION_MESSAGE_RECEIVED"

        /**
         * Build and send [ACTION_MESSAGE_RECEIVED] broadcast to all manifest receivers.
         * @param context [Context].
         * @param sourceUid The ID of the watch that sent the message.
         * @param message The com.boswelja.watchconnection.common.message received.
         * @param data The data sent with the message, or null if there was no data.
         */
        @SuppressLint("QueryPermissionsNeeded")
        fun sendBroadcast(context: Context, sourceUid: String, message: String, data: ByteArray?) {
            Intent(ACTION_MESSAGE_RECEIVED).apply {
                putExtra(EXTRA_WATCH_ID, sourceUid)
                putExtra(EXTRA_MESSAGE, message)
                if (data?.isNotEmpty() == true) putExtra(EXTRA_DATA, data)
            }.also { intent ->
                // Get all registered message receivers and send the intent to them. We can suppress
                // query permission warning since we're only targeting the package this lib is in.
                context.packageManager.queryBroadcastReceivers(intent, 0).forEach { info ->
                    val component = ComponentName(
                        info.activityInfo.packageName,
                        info.activityInfo.name
                    )
                    intent.component = component
                    context.sendBroadcast(intent)
                }
            }
        }
    }
}
