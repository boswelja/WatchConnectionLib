package com.boswelja.watchconnection.wearos.message

import android.content.Context
import com.boswelja.watchconnection.common.message.Message
import com.boswelja.watchconnection.common.message.MessagePriority
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.boswelja.watchconnection.common.message.serialized.MessageSerializer
import com.boswelja.watchconnection.core.Phone
import com.boswelja.watchconnection.core.message.BaseMessageClient
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageOptions
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * A [BaseMessageClient] for sending and receiving messages from a Wear OS smartwatch.
 * @param context [Context].
 * @param serializers The list of [MessageSerializer]s to use by default for serialization.
 */
class MessageClient(
    context: Context,
    serializers: List<MessageSerializer<*>>
) : BaseMessageClient(serializers) {

    private val nodeClient by lazy { Wearable.getNodeClient(context.applicationContext) }
    private val messageClient = Wearable.getMessageClient(context.applicationContext)
    private var phone: Phone? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun rawIncomingMessages(): Flow<ReceivedMessage<ByteArray?>> = callbackFlow {
        val listener = MessageClient.OnMessageReceivedListener {
            val data = if (it.data.isNotEmpty()) it.data else null
            trySend(
                ReceivedMessage(Phone.uidFor(it.sourceNodeId), it.path, data)
            )
        }
        messageClient.addListener(listener)
        awaitClose {
            messageClient.removeListener(listener)
        }
    }

    override suspend fun sendRawMessage(
        message: Message<ByteArray?>,
        priority: MessagePriority
    ): Boolean {
        return try {
            if (phone == null) {
                val node = nodeClient.connectedNodes.await().first()
                phone = Phone(node.displayName, node.id)
            }
            messageClient.sendMessage(
                phone!!.internalId,
                message.path,
                message.data,
                MessageOptions(priority.toGMSPriority())
            ).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * Convert a [MessagePriority] to one of [MessageOptions] priority constants.
 */
internal fun MessagePriority.toGMSPriority(): Int {
    return when (this) {
        MessagePriority.HIGH -> MessageOptions.MESSAGE_PRIORITY_HIGH
        MessagePriority.LOW -> MessageOptions.MESSAGE_PRIORITY_LOW
    }
}
