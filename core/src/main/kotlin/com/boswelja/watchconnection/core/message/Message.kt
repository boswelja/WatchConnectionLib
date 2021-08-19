package com.boswelja.watchconnection.core.message

import java.util.UUID

/**
 * A data class containing information about a received message.
 * @param sourceWatchId The [com.boswelja.watchconnection.core.Watch.id] of the watch that sent the
 * message.
 * @param message The message itself.
 * @param data Any data that may have been included with the message, or null if there is none.
 */
data class Message(
    val sourceWatchId: UUID,
    val message: String,
    val data: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Message

        if (sourceWatchId != other.sourceWatchId) return false
        if (message != other.message) return false
        if (data != null) {
            if (other.data == null) return false
            if (!data.contentEquals(other.data)) return false
        } else if (other.data != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sourceWatchId.hashCode()
        result = 31 * result + message.hashCode()
        result = 31 * result + (data?.contentHashCode() ?: 0)
        return result
    }
}
