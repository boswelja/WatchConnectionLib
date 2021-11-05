package com.boswelja.watchconnection.common.message

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.boswelja.watchconnection.common.message.MessageReceiverUtils.MessageReceived
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

public abstract class MessageReceiver : BroadcastReceiver() {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    public abstract suspend fun onMessageReceived(
        context: Context,
        message: ReceivedMessage<ByteArray?>
    )

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent?.action != MessageReceived) return

        // Get message details
        val message = MessageReceiverUtils.getReceivedMessageFromIntent(intent)

        val pendingResult = goAsync()
        coroutineScope.launch {
            try {
                onMessageReceived(
                    context,
                    message
                )
            } finally {
                pendingResult.finish()
                coroutineScope.cancel()
            }
        }
    }
}
