package com.boswelja.watchconnection.wearos.message

import android.content.Context
import android.net.Uri
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.MessageOptions
import kotlin.random.Random

internal class DummyMessageClient(
    context: Context
) : MessageClient(context, Settings.DEFAULT_SETTINGS) {

    val listeners = mutableListOf<OnMessageReceivedListener>()
    val messages = mutableListOf<ReceivedMessage<ByteArray?>>()

    override fun sendMessage(target: String, path: String, data: ByteArray?): Task<Int> {
        return sendMessage(target, path, data, MessageOptions(MessageOptions.MESSAGE_PRIORITY_LOW))
    }

    override fun sendMessage(
        target: String,
        path: String,
        data: ByteArray?,
        options: MessageOptions
    ): Task<Int> {
        messages.add(ReceivedMessage(target, path, data))
        return Tasks.forResult(0)
    }

    override fun addListener(listener: OnMessageReceivedListener): Task<Void> {
        return addListener(listener, Uri.EMPTY, 0)
    }

    override fun addListener(listener: OnMessageReceivedListener, p1: Uri, p2: Int): Task<Void> {
        listeners.add(listener)
        return Tasks.forResult(null)
    }

    override fun removeListener(listener: OnMessageReceivedListener): Task<Boolean> {
        val removed = listeners.remove(listener)
        return Tasks.forResult(removed)
    }
}

internal class DummyMessageEvent(
    private val sourceId: String,
    private val path: String,
    private val data: ByteArray?
) : MessageEvent {
    override fun getRequestId(): Int = 0
    override fun getPath(): String = path
    override fun getData(): ByteArray? = data
    override fun getSourceNodeId(): String = sourceId
}

/**
 * Returns a list of generated [ReceivedMessage].
 */
internal fun createMessagesFor(
    count: Int,
    platform: String
): List<ReceivedMessage<ByteArray?>> {
    return (0 until count).map {
        ReceivedMessage(
            platform + it.toString(),
            it.toString(),
            Random.nextBytes(8)
        )
    }
}
