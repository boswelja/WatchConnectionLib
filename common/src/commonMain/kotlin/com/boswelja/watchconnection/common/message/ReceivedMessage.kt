package com.boswelja.watchconnection.common.message

public data class ReceivedMessage<T>(
    val sourceUid: String,
    val path: String,
    val data: T
)
