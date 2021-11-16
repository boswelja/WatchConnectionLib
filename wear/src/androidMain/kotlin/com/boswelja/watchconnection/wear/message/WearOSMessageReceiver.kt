package com.boswelja.watchconnection.wear.message

import com.boswelja.watchconnection.common.message.MessageReceiverUtils
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

/**
 * A [WearableListenerService] that receives messages and broadcasts them to MessageReceiver.
 */
public class WearOSMessageReceiver : WearableListenerService() {
    override fun onMessageReceived(messageEvent: MessageEvent) {
        MessageReceiverUtils.sendMessageReceivedBroadcast(
            this,
            messageEvent.sourceNodeId,
            messageEvent.path,
            messageEvent.data
        )
    }
}
