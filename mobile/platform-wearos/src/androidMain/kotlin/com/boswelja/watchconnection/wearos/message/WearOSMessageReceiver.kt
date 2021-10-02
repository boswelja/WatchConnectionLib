package com.boswelja.watchconnection.wearos.message

import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.common.internal.MessageReceiverUtils
import com.boswelja.watchconnection.wearos.Constants.WEAROS_PLATFORM
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

public class WearOSMessageReceiver : WearableListenerService() {
    override fun onMessageReceived(messageEvent: MessageEvent) {
        val watch = Watch(
            "",
            messageEvent.sourceNodeId,
            WEAROS_PLATFORM
        )
        MessageReceiverUtils.sendMessageReceivedBroadcast(
            this,
            watch.uid,
            messageEvent.path,
            messageEvent.data
        )
    }
}
