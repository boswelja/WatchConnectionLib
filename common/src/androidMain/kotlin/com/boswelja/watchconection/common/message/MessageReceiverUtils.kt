package com.boswelja.watchconection.common.message

import android.content.Context
import android.content.Intent
import com.boswelja.watchconnection.common.message.ReceivedMessage

public object MessageReceiverUtils {

    public const val MessageReceived: String =
        "com.boswelja.watchconnection.common.message.MessageReceived"

    private const val SenderUid: String = "senderuid"
    private const val MessagePath: String = "messagepath"
    private const val MessageData: String = "messagedata"

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

    public fun getReceivedMessageFromIntent(source: Intent): ReceivedMessage<ByteArray?> {
        val senderUid = source.getStringExtra(SenderUid)
        requireNotNull(senderUid) { "Didn't get a sender UID with message intent!" }
        val messagePath = source.getStringExtra(MessagePath)
        requireNotNull(messagePath) { "Didn't get a message path with message intent!" }
        val messageData = source.getByteArrayExtra(MessageData)
        return ReceivedMessage(
            senderUid,
            messagePath,
            messageData
        )
    }
}
