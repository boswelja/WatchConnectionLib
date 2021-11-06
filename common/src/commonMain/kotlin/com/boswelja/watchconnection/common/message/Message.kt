package com.boswelja.watchconnection.common.message

/**
 * Defines a message to be sent. For incoming messages, see [ReceivedMessage].
 * @param path The path to send the message to.
 * @param data The data to send with the message. If this is not a [ByteArray] or null, a serializer
 * must be used.
 * @param priority The message priority. See [Priority].
 */
public data class Message<out T>(
    val path: String,
    val data: T,
    val priority: Priority = Priority.LOW
) {

    /**
     * Constants for defining a message priority. Behaviour may vary between platforms.
     */
    public enum class Priority {
        /**
         * Indicates the message is low priority. The platform may delay low priority messages in
         * order to optimise performance and battery.
         */
        LOW,

        /**
         * Indicates the message is high priority. The platform should prioritise sending the
         * message as soon as possible.
         */
        HIGH
    }
}
