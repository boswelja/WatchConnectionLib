package com.boswelja.watchconnection.common.message

/**
 * A data class containing information about a received message.
 * message.
 * @param path The message itself.
 * @param data Any data that may have been included with the message, or null if there is none.
 */
data class ByteArrayMessage(
    override val path: String,
    override val data: ByteArray? = null
) : Message<ByteArray?>(path, data) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ByteArrayMessage

        if (path != other.path) return false
        if (data != null) {
            if (other.data == null) return false
            if (!data.contentEquals(other.data)) return false
        } else if (other.data != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + (data?.contentHashCode() ?: 0)
        return result
    }
}
