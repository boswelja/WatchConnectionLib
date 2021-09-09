package com.boswelja.watchconnection.common.message

data class Message<out T>(
    val path: String,
    val data: T
)
