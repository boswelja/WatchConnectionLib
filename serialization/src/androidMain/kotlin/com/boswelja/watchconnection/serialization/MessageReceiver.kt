package com.boswelja.watchconnection.serialization

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.boswelja.watchconection.common.message.MessageReceiverUtils
import com.boswelja.watchconnection.common.message.ReceivedMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

public abstract class MessageReceiver<T>(
    private val serializer: MessageSerializer<T>
) : BroadcastReceiver() {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    public abstract suspend fun onMessageReceived(
        context: Context,
        message: ReceivedMessage<T>
    )

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent?.action != MessageReceiverUtils.MessageReceived) return

        // Get message details
        val message = MessageReceiverUtils.getReceivedMessageFromIntent(intent)

        val pendingResult = goAsync()
        coroutineScope.launch {
            // Deserialize and pass message on
            val deserializedData = serializer.deserialize(message.data)
            try {
                onMessageReceived(
                    context,
                    ReceivedMessage(
                        message.sourceUid,
                        message.path,
                        deserializedData
                    )
                )
            } finally {
                pendingResult.finish()
                coroutineScope.cancel()
            }
        }
    }
}
