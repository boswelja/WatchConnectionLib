package com.boswelja.watchconnection.common.message

import android.content.Context
import android.content.Intent

/**
 * Contains helper values and functions to enable BroadcastReceivers to receive messages.
 */
public object MessageReceiverUtils {

    /**
     * An intent action fired when a message is received.
     */
    public const val MessageReceived: String =
        "com.boswelja.watchconnection.common.message.MessageReceived"

    internal const val SenderUid: String = "senderuid"
    internal const val MessagePath: String = "messagepath"
    internal const val MessageData: String = "messagedata"

    /**
     * Broadcast an intent containing the specified message details. Intents can be received by any
     * BroadcastReceiver within the package that receive [MessageReceived].
     * @param context [Context].
     * @param senderUid The device UID that sent the message.
     * @param messagePath The path for the received message.
     * @param messageBytes The received message data, or null if there is none.
     */
    public fun sendMessageReceivedBroadcast(
        context: Context,
        senderUid: String,
        messagePath: String,
        messageBytes: ByteArray?
    ) {
        require(senderUid.isNotBlank())
        require(messagePath.isNotBlank())

        val intent = Intent(MessageReceived).apply {
            putExtra(SenderUid, senderUid)
            putExtra(MessagePath, messagePath)
            putExtra(MessageData, messageBytes)
            setPackage(context.packageName)
        }
        context.sendBroadcast(intent)
    }

    /**
     * Get received message data from the given Intent.
     * @param source The intent to pull data from.
     * @return The received message.
     */
    public fun getReceivedMessageFromIntent(source: Intent): ReceivedMessage<ByteArray?> {
        val senderUid = source.getStringExtra(SenderUid)
        require(!senderUid.isNullOrBlank()) { "Didn't get a sender UID with message intent!" }
        val messagePath = source.getStringExtra(MessagePath)
        require(!messagePath.isNullOrBlank()) { "Didn't get a message path with message intent!" }
        val messageData = source.getByteArrayExtra(MessageData)
        return ReceivedMessage(
            senderUid,
            messagePath,
            messageData
        )
    }
}
