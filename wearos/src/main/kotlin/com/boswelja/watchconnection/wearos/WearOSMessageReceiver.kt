package com.boswelja.watchconnection.wearos

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.boswelja.watchconnection.core.MessageReceiver
import com.boswelja.watchconnection.core.Messages
import com.boswelja.watchconnection.core.Watch
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

/**
 * A [WearableListenerService] to parse [MessageEvent]s and hand them off to
 * [Messages.ACTION_MESSAGE_RECEIVED] receivers.
 */
class WearOSMessageReceiver : WearableListenerService() {
    override fun onMessageReceived(messageEvent: MessageEvent?) {
        messageEvent?.let { event ->
            // Get watch ID
            val watchId = Watch.createUUID(WearOSPlatform.PLATFORM, event.sourceNodeId)

            // Build intent
            Intent(Messages.ACTION_MESSAGE_RECEIVED).apply {
                putExtra(MessageReceiver.WATCH_ID_EXTRA, watchId.toString())
                putExtra(MessageReceiver.MESSAGE_EXTRA, event.path)
                if (event.data.isNotEmpty()) putExtra(MessageReceiver.DATA_EXTRA, event.data)
            }.also {
                // Send to receivers in this app specifically
                LocalBroadcastManager.getInstance(this).sendBroadcast(it)
            }
        }
    }
}
