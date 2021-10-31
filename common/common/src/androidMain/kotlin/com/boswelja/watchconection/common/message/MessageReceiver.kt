package com.boswelja.watchconection.common.message

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.boswelja.watchconection.common.message.MessageReceiverUtils.MessageData
import com.boswelja.watchconection.common.message.MessageReceiverUtils.MessagePath
import com.boswelja.watchconection.common.message.MessageReceiverUtils.MessageReceived
import com.boswelja.watchconection.common.message.MessageReceiverUtils.SenderUid
import com.boswelja.watchconnection.common.message.MessageSerializer
import com.boswelja.watchconnection.common.message.ReceivedMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

public abstract class MessageReceiver<T>(
    private val serializer: MessageSerializer<T>
) : BroadcastReceiver() {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    public abstract suspend fun onMessageReceived(context: Context, message: ReceivedMessage<T>)

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent?.action != MessageReceived) return

        // Get message details
        val senderUid = intent.getStringExtra(SenderUid)
        requireNotNull(senderUid) { "Didn't get a sender UID with message intent!" }
        val messagePath = intent.getStringExtra(MessagePath)
        requireNotNull(messagePath) { "Didn't get a message path with message intent!" }
        val messageData = intent.getByteArrayExtra(MessageData)
        requireNotNull(messageData) { "Didn't get message data with message intent!" }

        val pendingResult = goAsync()
        coroutineScope.launch {
            // Deserialize and pass message on
            val deserializedData = serializer.deserialize(messageData)
            try {
                onMessageReceived(
                    context,
                    ReceivedMessage(
                        senderUid,
                        messagePath,
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
