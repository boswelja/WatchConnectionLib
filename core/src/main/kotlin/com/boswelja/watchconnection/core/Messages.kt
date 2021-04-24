package com.boswelja.watchconnection.core

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import java.util.UUID
import android.content.ComponentName

object Messages {
    const val ACTION_MESSAGE_RECEIVED =
        "com.boswelja.watchconnection.messages.ACTION_MESSAGE_RECEIVED"

    @SuppressLint("QueryPermissionsNeeded")
    fun sendBroadcast(context: Context, watchId: UUID, message: String, data: ByteArray?) {
        Intent(ACTION_MESSAGE_RECEIVED).apply {
            putExtra(MessageReceiver.WATCH_ID_EXTRA, watchId.toString())
            putExtra(MessageReceiver.MESSAGE_EXTRA, message)
            if (data?.isNotEmpty() == true) putExtra(MessageReceiver.DATA_EXTRA, data)
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
