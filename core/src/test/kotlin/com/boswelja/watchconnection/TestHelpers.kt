package com.boswelja.watchconnection

import com.boswelja.watchconnection.common.Watch

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
