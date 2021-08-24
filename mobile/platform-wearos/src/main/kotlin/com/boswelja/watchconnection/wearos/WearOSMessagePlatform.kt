package com.boswelja.watchconnection.wearos

import android.content.Context
import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.common.message.MessagePriority
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

class WearOSMessagePlatform(
    private val messageClient: MessageClient,
) : MessagePlatform {

    constructor(context: Context) : this(Wearable.getMessageClient(context))

    override val platformIdentifier: String = WEAROS_PLATFORM

    @ExperimentalCoroutinesApi
    override fun incomingMessages(): Flow<ReceivedMessage<ByteArray?>> = callbackFlow {
        val listener = MessageClient.OnMessageReceivedListener { messageEvent ->
            val message = ReceivedMessage(
                Watch.createUUID(platformIdentifier, messageEvent.sourceNodeId),
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
        priority: MessagePriority
    ): Boolean {
        // Either sendMessage is successful, or ApiException is thrown
        return try {
            val priorityInt = when (priority) {
                MessagePriority.LOW -> MessageOptions.MESSAGE_PRIORITY_LOW
                MessagePriority.HIGH -> MessageOptions.MESSAGE_PRIORITY_HIGH
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
