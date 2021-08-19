package com.boswelja.watchconnection.core.message

import java.util.UUID

class ReceivedMessage<T>(
    val sourceWatchID: UUID,
    val path: String,
    val data: T
)
