package com.boswelja.watchconnection.wear.message

import android.content.Context
import com.boswelja.watchconnection.common.message.Message
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

public class MessageClient(
    context: Context
) : com.boswelja.watchconnection.common.message.MessageClient {

    private val messageClient = Wearable.getMessageClient(context.applicationContext)

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
        messageClient.addListener(listener)

        // Unregister the listener on close
        awaitClose {
            messageClient.removeListener(listener)
        }
    }

    override suspend fun sendMessage(targetUid: String, message: Message<ByteArray?>): Boolean {
        return try {
            // TODO handle priority
            messageClient.sendMessage(targetUid, message.path, message.data).await()
            // If we get this far, sending was successful
            true
        } catch (e: Exception) {
            false
        }
    }
}
