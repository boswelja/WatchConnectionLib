package com.boswelja.watchconnection.wearos.message

import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.google.android.gms.wearable.MessageEvent
import kotlin.random.Random

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
            Watch.createUid(platform, it.toString()),
            it.toString(),
            Random.nextBytes(8)
        )
    }
}
