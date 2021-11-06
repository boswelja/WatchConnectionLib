package com.boswelja.watchconnection.core

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

fun createCapabilities(count: Int): Set<String> {
    return (0 until count).map { "capability$it" }.toSet()
}

expect fun runBlockingTest(test: suspend () -> Unit)
