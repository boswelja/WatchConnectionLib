package com.boswelja.watchconnection.common.message

public data class Message<out T>(
    val path: String,
    val data: T
)
