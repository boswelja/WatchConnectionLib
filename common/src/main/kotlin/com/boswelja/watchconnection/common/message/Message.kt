package com.boswelja.watchconnection.common.message

abstract class Message<out T>(
    open val path: String,
    open val data: T
)
