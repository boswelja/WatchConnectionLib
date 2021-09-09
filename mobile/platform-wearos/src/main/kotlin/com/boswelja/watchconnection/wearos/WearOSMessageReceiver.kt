package com.boswelja.watchconnection.wearos

import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

/**
 * A [WearableListenerService] to parse [MessageEvent]s and hand them off to
 * [Messages.ACTION_MESSAGE_RECEIVED] receivers.
 */
class WearOSMessageReceiver : WearableListenerService() {
    override fun onMessageReceived(messageEvent: MessageEvent?) {
//        messageEvent?.let { event ->
//            // TODO Fix this
//            // Get watch ID
//            val watchId = Watch.createUUID(WEAROS_PLATFORM, event.sourceNodeId)
//
//            // Send message broadcast
//            MessageReceiver.sendBroadcast(this, watchId, event.path, event.data)
//        }
    }
}
