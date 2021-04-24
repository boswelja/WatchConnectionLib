package com.boswelja.watchconnection.core

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import java.util.UUID

object Messages {

    /**
     * Sent when a message is received from any supported [WatchPlatform]. Implement
     * [MessageReceiver] to receive this intent.
     */
    const val ACTION_MESSAGE_RECEIVED =
        "com.boswelja.watchconnection.messages.ACTION_MESSAGE_RECEIVED"

    /**
     * Build and send [ACTION_MESSAGE_RECEIVED] broadcast to all manifest receivers. This should not
     * be called from application code, and is only exposed for [WatchPlatform] use.
     * @param context [Context].
     * @param watchId The ID of the watch that sent the message. See [Watch.id].
     * @param message The message received.
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
