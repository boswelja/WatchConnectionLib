package com.boswelja.watchconnection.wear.message

import android.content.Context
import com.boswelja.watchconnection.common.message.Message
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.boswelja.watchconnection.common.message.serialized.MessageSerializer
import com.boswelja.watchconnection.wear.Phone
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

public actual class MessageClient(
    context: Context,
    private val serializers: List<MessageSerializer<*>>
) {

    private val messageClient = Wearable.getMessageClient(context.applicationContext)

    @OptIn(ExperimentalCoroutinesApi::class)
    public actual fun <T> incomingMessages(
        serializer: MessageSerializer<T>
    ): Flow<ReceivedMessage<T>> = callbackFlow {
        // Create a message listener
        val listener = MessageClient.OnMessageReceivedListener {
            if (serializer.messagePaths.contains(it.path)) {
                val deserializedData = runBlocking { serializer.deserialize(it.data) }
                val receivedMessage = ReceivedMessage(
                    it.sourceNodeId, it.path, deserializedData
                )
                trySend(receivedMessage)
            }
        }

        // Register the listener
        messageClient.addListener(listener)

        // Unregister the listener on close
        awaitClose {
            messageClient.removeListener(listener)
        }
    }

    public actual suspend fun <T> sendMessage(
        targetPhone: Phone,
        message: Message<T>
    ): Boolean {
        val serializer = serializers.firstOrNull { it.messagePaths.contains(message.path) }
        val bytes = if (serializer != null) {
            requireNotNull(message.data) { "Expected data with message ${message.path}" }
            serializer.serializeAny(message.data!!)
        } else {
            require(message.data is ByteArray?) { "No serializer found for ${message.path}" }
            message.data as ByteArray?
        }
        return try {
            messageClient.sendMessage(targetPhone.uid, message.path, bytes).await()
            // If we get this far, sending was successful
            true
        } catch (e: Exception) {
            false
        }
    }
}
