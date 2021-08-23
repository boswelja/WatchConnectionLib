package com.boswelja.watchconnection.core.message.serialized

import android.content.BroadcastReceiver
import android.content.Context
import com.boswelja.watchconnection.core.message.MessageReceiver
import com.boswelja.watchconnection.core.message.ReceivedMessage

/**
 * An extension of [MessageReceiver] that supports deserializing data automatically.
 */
abstract class TypedMessageReceiver<T>(
    private val serializer: MessageSerializer<T>
) : MessageReceiver() {

    /**
     * Called when a message has been received. While this is a suspendable function, the limits
     * of [BroadcastReceiver.goAsync] still apply.
     * @param message The [ReceivedMessage] that was received.
     */
    abstract suspend fun onTypedMessageReceived(context: Context, message: ReceivedMessage<T>)

    /**
     * Called when [serializer] thows an exception deserializing data. When overriding this, do not
     * call super. The default behavior is to throw the exception.
     * @param exception The [Exception] thrown.
     */
    open suspend fun onDeserializeException(exception: Exception) {
        throw exception
    }

    final override suspend fun onMessageReceived(
        context: Context,
        message: ReceivedMessage<ByteArray?>
    ) {
        if (serializer.messagePaths.contains(message.path)) {
            requireNotNull(message.data) { "Expected data with message $message" }

            // Deserialize data
            val data = try {
                serializer.deserialize(message.data)
            } catch (e: Exception) {
                onDeserializeException(exception = e)
                null
            }

            if (data != null) {
                // Pass the deserialized message on
                onTypedMessageReceived(
                    context,
                    ReceivedMessage(
                        message.sourceWatchID,
                        message.path,
                        data
                    )
                )
            }
        }
    }
}
