package com.boswelja.watchconnection.core.message.serialized

import com.boswelja.watchconnection.core.message.Message

/**
 * A data class containing information about a message.
 * message.
 * @param path The message path.
 * @param data Any data that may have been included with the message.
 */
data class TypedMessage<T>(
    override val path: String,
    override val data: T
) : Message<T>(path, data)
