package com.boswelja.watchconnection.serialization

import kotlinx.coroutines.runBlocking

internal actual fun runBlockingTest(test: suspend () -> Unit) {
    runBlocking {
        test()
    }
}
