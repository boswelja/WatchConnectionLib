package com.boswelja.watchconnection.core.message

abstract class Message<T>(
    open val path: String,
    open val data: T
)
