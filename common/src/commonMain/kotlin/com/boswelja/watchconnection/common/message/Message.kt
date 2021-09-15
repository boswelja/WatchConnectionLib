package com.boswelja.watchconnection.common.message

public data class Message<out T>(
    val path: String,
    val data: T,
    val priority: Priority = Priority.LOW
) {

    /**
     * Constants for defining a message priority.
     */
    public enum class Priority {
        /**
         * Indicates the message is of low priority, and can be delayed.
         */
        LOW,

        /**
         * Indicates the message is of high priority, and should not be delayed.
         */
        HIGH
    }
}
