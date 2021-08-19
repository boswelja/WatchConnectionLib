package com.boswelja.watchconnection.core.message.serialized

import com.boswelja.watchconnection.core.message.Message
import java.util.UUID

/**
 * A data class containing information about a received message.
 * @param sourceWatchID The [com.boswelja.watchconnection.core.Watch.id] of the watch that sent the
 * message.
 * @param path The message itself.
 * @param data Any data that may have been included with the message, or null if there is none.
 */
data class TypedMessage<T>(
    override val sourceWatchID: UUID,
    override val path: String,
    override val data: T
) : Message<T>(sourceWatchID, path, data)
