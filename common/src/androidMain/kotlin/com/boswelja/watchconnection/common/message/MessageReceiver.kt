package com.boswelja.watchconnection.common.message

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
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
        if (intent?.action != ACTION_MESSAGE_RECEIVED) return

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

        /**
         * Sent when a message is received from any supported [MessagePlatform]. Implement
         * [MessageReceiver] to receive this intent.
         */
        const val ACTION_MESSAGE_RECEIVED =
            "com.boswelja.watchconnection.messages.ACTION_MESSAGE_RECEIVED"

        /**
         * Build and send [ACTION_MESSAGE_RECEIVED] broadcast to all manifest receivers. This should not
         * be called from application code, and is only exposed for [MessagePlatform] use.
         * @param context [Context].
         * @param watchId The ID of the watch that sent the message. See
         * [com.boswelja.watchconnection.core.Watch.id].
         * @param message The com.boswelja.watchconnection.common.message received.
         * @param data The data sent with the message, or null if there was no data.
         */
        @SuppressLint("QueryPermissionsNeeded")
        fun sendBroadcast(context: Context, watchId: UUID, message: String, data: ByteArray?) {
            Intent(ACTION_MESSAGE_RECEIVED).apply {
                putExtra(MessageReceiver.EXTRA_WATCH_ID, watchId.toString())
                putExtra(MessageReceiver.EXTRA_MESSAGE, message)
                if (data?.isNotEmpty() == true) putExtra(MessageReceiver.EXTRA_DATA, data)
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
