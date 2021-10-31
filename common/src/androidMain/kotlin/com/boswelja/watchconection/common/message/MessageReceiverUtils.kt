package com.boswelja.watchconection.common.message

import android.content.Context
import android.content.Intent

public object MessageReceiverUtils {

    public const val MessageReceived: String =
        "com.boswelja.watchconnection.common.message.MessageReceived"

    public const val SenderUid: String = "senderuid"
    public const val MessagePath: String = "messagepath"
    public const val MessageData: String = "messagedata"

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
}
