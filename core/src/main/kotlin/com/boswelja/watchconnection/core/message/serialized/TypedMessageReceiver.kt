package com.boswelja.watchconnection.core.message.serialized

import android.content.BroadcastReceiver
import android.content.Context
import com.boswelja.watchconnection.core.message.Message
import com.boswelja.watchconnection.core.message.MessageReceiver

/**
 * An extension of [MessageReceiver] that supports deserializing data automatically.
 */
abstract class TypedMessageReceiver<T>(
    private val messages: Set<String>,
    private val serializer: DataSerializer<T>
) : MessageReceiver() {

    /**
     * Called when a message has been received. While this is a suspendable function, the limits
     * of [BroadcastReceiver.goAsync] still apply.
     * @param message The [TypedMessage] that was received.
     */
    abstract suspend fun onMessageReceived(context: Context, message: TypedMessage<T>)

    final override suspend fun onMessageReceived(context: Context, message: Message) {
        if (messages.contains(message.message) && message.data != null) {

            // Deserialize data
            val data = serializer.deserialize(message.data)

            // Pass the TypedMessage on
            onMessageReceived(
                context,
                TypedMessage(
                    message.sourceWatchId,
                    message.message,
                    data
                )
            )
        }
    }
}
