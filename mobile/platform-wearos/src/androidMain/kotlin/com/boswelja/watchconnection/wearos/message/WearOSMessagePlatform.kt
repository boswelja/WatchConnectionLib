package com.boswelja.watchconnection.wearos.message

import android.content.Context
import com.boswelja.watchconnection.common.message.Message
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.boswelja.watchconnection.core.message.MessagePlatform
import com.boswelja.watchconnection.wearos.Constants.WEAROS_PLATFORM
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageOptions
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

public actual class WearOSMessagePlatform(
    private val messageClient: MessageClient,
) : MessagePlatform {

    public constructor(context: Context) : this(Wearable.getMessageClient(context))

    override val platformIdentifier: String = WEAROS_PLATFORM

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun incomingMessages(): Flow<ReceivedMessage<ByteArray?>> = callbackFlow {
        val listener = MessageClient.OnMessageReceivedListener { messageEvent ->
            val message = ReceivedMessage(
                platformIdentifier + messageEvent.sourceNodeId, // TODO This is a no-go
                messageEvent.path,
                messageEvent.data
            )
            trySendBlocking(message)
        }

        messageClient.addListener(listener)

        awaitClose {
            messageClient.removeListener(listener)
        }
    }

    override suspend fun sendMessage(
        watchId: String,
        message: String,
        data: ByteArray?,
        priority: Message.Priority
    ): Boolean {
        // Either sendMessage is successful, or ApiException is thrown
        return try {
            val priorityInt = when (priority) {
                Message.Priority.LOW -> MessageOptions.MESSAGE_PRIORITY_LOW
                Message.Priority.HIGH -> MessageOptions.MESSAGE_PRIORITY_HIGH
            }
            val options = MessageOptions(priorityInt)
            messageClient.sendMessage(watchId, message, data, options).await()
            // If we get here, message send was successful
            true
        } catch (e: ApiException) {
            false
        }
    }
}
