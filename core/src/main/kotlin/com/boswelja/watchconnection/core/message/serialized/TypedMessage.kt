package com.boswelja.watchconnection.core.message.serialized

import com.boswelja.watchconnection.core.message.Message

/**
 * A data class containing information about a received message.
 * message.
 * @param path The message itself.
 * @param data Any data that may have been included with the message, or null if there is none.
 */
data class TypedMessage<T>(
    override val path: String,
    override val data: T
) : Message<T>(path, data)
