package com.boswelja.watchconnection

import com.boswelja.watchconnection.core.Watch
import com.boswelja.watchconnection.core.message.ByteArrayMessage
import kotlin.random.Random

fun createWatchesFor(count: Int, platformIdentifier: String): List<Watch> {
    return (0 until count).map {
        Watch(
            name = "Watch $it",
            "platform$count",
            platformIdentifier
        )
    }
}

fun createCapabilities(count: Int): List<String> {
    return (0 until count).map { "capability$it" }
}

/**
 * Returns a list of pairs. Each pair contains the source watch ID (note this is not the watches UID
 * assigned by WatchConnectionLib), and a fake [ByteArrayMessage].
 */
fun createMessagesFor(count: Int, platform: String): List<Pair<String, ByteArrayMessage>> {
    return (0 until count).map {
        Pair(
            it.toString(),
            ByteArrayMessage(
                Watch.createUUID(platform, it.toString()),
                it.toString(),
                Random.nextBytes(8)
            )
        )
    }
}
