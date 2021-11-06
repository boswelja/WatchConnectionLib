package com.boswelja.watchconnection.wear.message

import android.content.Context
import com.boswelja.watchconnection.common.message.Message
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageOptions
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * A [com.boswelja.watchconnection.common.message.MessageClient] implementation for Android-powered
 * Wearables.
 */
public class MessageClient(
    context: Context
) : com.boswelja.watchconnection.common.message.MessageClient {

    private val wearableMessageClient = Wearable.getMessageClient(context.applicationContext)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun incomingMessages(): Flow<ReceivedMessage<ByteArray?>> = callbackFlow {
        // Create a message listener
        val listener = MessageClient.OnMessageReceivedListener {
            val bytes: ByteArray? = it.data
            val receivedMessage = ReceivedMessage(
                it.sourceNodeId, it.path, bytes
            )
            trySend(receivedMessage)
        }

        // Register the listener
        wearableMessageClient.addListener(listener)

        // Unregister the listener on close
        awaitClose {
            wearableMessageClient.removeListener(listener)
        }
    }

    override suspend fun sendMessage(targetUid: String, message: Message<ByteArray?>): Boolean {
        return try {
            val priorityInt = when (message.priority) {
                Message.Priority.LOW -> MessageOptions.MESSAGE_PRIORITY_LOW
                Message.Priority.HIGH -> MessageOptions.MESSAGE_PRIORITY_HIGH
            }
            val options = MessageOptions(priorityInt)
            wearableMessageClient.sendMessage(targetUid, message.path, message.data, options).await()
            // If we get this far, sending was successful
            true
        } catch (_: Exception) {
            false
        }
    }
}
