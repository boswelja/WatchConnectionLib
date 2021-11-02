package com.boswelja.watchconnection.serialization

internal expect fun runBlockingTest(test: suspend () -> Unit)
