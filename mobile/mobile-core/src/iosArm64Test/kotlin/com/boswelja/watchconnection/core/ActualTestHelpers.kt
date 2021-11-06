package com.boswelja.watchconnection.core

import kotlinx.coroutines.runBlocking

public actual fun runBlockingTest(test: suspend () -> Unit) {
    runBlocking {
        test()
    }
}
