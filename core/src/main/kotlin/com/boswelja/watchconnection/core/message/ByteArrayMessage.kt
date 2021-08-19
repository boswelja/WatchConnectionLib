package com.boswelja.watchconnection.core.message

import java.util.UUID

/**
 * A data class containing information about a received message.
 * @param sourceWatchID The [com.boswelja.watchconnection.core.Watch.id] of the watch that sent the
 * message.
 * @param path The message itself.
 * @param data Any data that may have been included with the message, or null if there is none.
 */
data class ByteArrayMessage(
    override val sourceWatchID: UUID,
    override val path: String,
    override val data: ByteArray? = null
) : Message<ByteArray?>(sourceWatchID, path, data) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ByteArrayMessage

        if (sourceWatchID != other.sourceWatchID) return false
        if (path != other.path) return false
        if (data != null) {
            if (other.data == null) return false
            if (!data.contentEquals(other.data)) return false
        } else if (other.data != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sourceWatchID.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + (data?.contentHashCode() ?: 0)
        return result
    }
}
