package com.boswelja.watchconnection.wearos

import kotlinx.coroutines.delay

suspend fun repeating(
    interval: Long,
    initialDelay: Long = 0,
    action: suspend () -> Unit
) {
    delay(initialDelay)
    while (true) {
        action()
        delay(interval)
    }
}
