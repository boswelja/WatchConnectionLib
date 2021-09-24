package com.boswelja.watchconnection.wear

import kotlinx.coroutines.delay

/**
 * Schedule a suspendable code block to be executed at a regular interval.
 * @param interval The interval in milliseconds to execute the code block after. Must be greater
 * than 0.
 * @param initialDelay The initial delay in milliseconds before first execution. Values less than or
 * equal to 0 are ignored.
 * @param action The suspendable code block to be executed.
 */
internal suspend fun repeating(
    interval: Long,
    initialDelay: Long = 0,
    action: suspend () -> Unit
) {
    if (interval <= 0) throw IllegalArgumentException("interval must be greater than 0")
    if (initialDelay > 0) delay(initialDelay)
    while (true) {
        action()
        delay(interval)
    }
}
