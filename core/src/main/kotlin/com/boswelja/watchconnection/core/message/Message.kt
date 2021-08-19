package com.boswelja.watchconnection.core.message

import java.util.UUID

abstract class Message<T>(
    open val sourceWatchID: UUID,
    open val path: String,
    open val data: T
)
