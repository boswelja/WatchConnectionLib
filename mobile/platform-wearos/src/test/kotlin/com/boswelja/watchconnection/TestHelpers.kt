package com.boswelja.watchconnection

import com.boswelja.watchconnection.common.message.ReceivedMessage
import kotlin.random.Random

/**
 * Returns a list of pairs. Each pair contains the source watch ID (note this is not the watches UID
 * assigned by WatchConnectionLib), and a fake [ReceivedMessage].
 */
fun createMessagesFor(
    count: Int,
    platform: String
): List<Pair<String, ReceivedMessage<ByteArray?>>> {
    return (0 until count).map {
        Pair(
            it.toString(),
            ReceivedMessage(
                platform + it.toString(),
                it.toString(),
                Random.nextBytes(8)
            )
        )
    }
}
