package com.boswelja.watchconnection.tizen

object Messages {
    private const val messageDelimiter = '|'.toByte()

    /**
     * Convert a message to a [ByteArray] for use with [TizenAccessoryAgent].
     */
    internal fun toByteArray(message: String, data: ByteArray?): ByteArray {
        var messageData = message.toByteArray(Charsets.UTF_8)
        // If data is not null, add delimiter and data to message
        data?.let {
            messageData += messageDelimiter
            messageData += data
        }
        return messageData
    }

    /**
     * Get a message from a [ByteArray] for use with [TizenAccessoryAgent].
     */
    internal fun fromByteArray(data: ByteArray): Pair<String, ByteArray?> {
        val delimiterIndex = data.indexOfFirst { it == messageDelimiter }
        return when {
            delimiterIndex <= -1 -> {
                // If delimiterIndex is -1, then we have no data
                Pair(String(data, Charsets.UTF_8), null)
            }
            delimiterIndex <= 0 -> {
                // If delimiter index is 0, we've got an invalid message
                throw IllegalArgumentException("Couldn't parse message identifier from data")
            }
            else -> {
                // In all other conditions, we have data
                val message = data.copyOfRange(0, delimiterIndex)
                val messageData = data.copyOfRange(delimiterIndex + 1, data.size)
                Pair(String(message, Charsets.UTF_8), messageData)
            }
        }
    }
}
