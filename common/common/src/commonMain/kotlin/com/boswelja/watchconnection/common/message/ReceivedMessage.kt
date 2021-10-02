package com.boswelja.watchconnection.common.message

/**
 * Contains data about a received message.
 * @param sourceUid The UID of the device that sent the message. See
 * [com.boswelja.watchconnection.common.Device.uid].
 * @param path The path of the received message.
 * @param data The data sent with the message.
 */
public data class ReceivedMessage<T>(
    val sourceUid: String,
    val path: String,
    val data: T
)
