package com.boswelja.watchconnection.core.message

abstract class Message<out T>(
    open val path: String,
    open val data: T
)
