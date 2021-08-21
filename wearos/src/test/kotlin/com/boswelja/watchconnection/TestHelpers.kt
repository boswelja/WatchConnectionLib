package com.boswelja.watchconnection

import com.boswelja.watchconnection.core.Watch
import com.boswelja.watchconnection.core.message.ByteArrayMessage
import com.boswelja.watchconnection.core.message.ReceivedMessage
import kotlin.random.Random

/**
 * Returns a list of pairs. Each pair contains the source watch ID (note this is not the watches UID
 * assigned by WatchConnectionLib), and a fake [ByteArrayMessage].
 */
fun createMessagesFor(
    count: Int,
    platform: String
): List<Pair<String, ReceivedMessage<ByteArray?>>> {
    return (0 until count).map {
        Pair(
            it.toString(),
            ReceivedMessage(
                Watch.createUUID(platform, it.toString()),
                it.toString(),
                Random.nextBytes(8)
            )
        )
    }
}
