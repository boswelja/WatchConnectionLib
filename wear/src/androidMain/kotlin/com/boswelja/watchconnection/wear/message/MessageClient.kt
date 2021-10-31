package com.boswelja.watchconnection.wear.message

import android.content.Context
import com.boswelja.watchconnection.common.Phone
import com.boswelja.watchconnection.common.message.Message
import com.boswelja.watchconnection.common.message.MessageSerializer
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await

public actual class MessageClient(
    context: Context,
    private val serializers: List<MessageSerializer<*>>
) : com.boswelja.watchconnection.common.message.MessageClient {

    private val messageClient = Wearable.getMessageClient(context.applicationContext)

    @OptIn(ExperimentalCoroutinesApi::class)
    public actual fun <T> incomingMessages(
        serializer: MessageSerializer<T>
    ): Flow<ReceivedMessage<T>> = rawIncomingMessages()
        .mapNotNull { message ->
            if (serializer.messagePaths.contains(message.path)) {
                val deserializedData = serializer.deserialize(message.data!!)
                ReceivedMessage(
                    message.sourceUid,
                    message.path,
                    deserializedData
                )
            } else null
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
        return sendMessage(targetPhone.uid, Message(message.path, bytes))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun rawIncomingMessages(): Flow<ReceivedMessage<ByteArray?>> = callbackFlow {
        // Create a message listener
        val listener = MessageClient.OnMessageReceivedListener {
            val receivedMessage = ReceivedMessage(
                it.sourceNodeId, it.path, it.data as ByteArray?
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
