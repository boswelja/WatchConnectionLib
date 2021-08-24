package com.boswelja.watchconnection.common.message

/**
 * Constants for defining a message priority.
 */
enum class MessagePriority {
    /**
     * Indicates the message is of low priority, and can be delayed.
     */
    LOW,

    /**
     * Indicates the message is of high priority, and should not be delayed.
     */
    HIGH
}
