package com.boswelja.watchconnection.core.message.serialized

import java.util.UUID

/**
 * A data class containing information about a received message.
 * @param sourceWatchId The [com.boswelja.watchconnection.core.Watch.id] of the watch that sent the
 * message.
 * @param message The message itself.
 * @param data Any data that may have been included with the message, or null if there is none.
 */
data class TypedMessage<T>(
    val sourceWatchId: UUID,
    val message: String,
    val data: T
)
