package com.boswelja.watchconnection.common.message

import kotlinx.coroutines.flow.Flow

public interface MessageClient {

    /**
     * A [Flow] of [ReceivedMessage]s received by all platforms. Messages collected here have no
     * additional processing performed, and thus only contain the raw data in bytes.
     */
    public fun incomingMessages(): Flow<ReceivedMessage<ByteArray?>>

    /**
     * Send a message to the device with the given UID.
     * @param targetUid The target device UID to send the message to.
     * @param message The [Message] to send.
     * @return true if sending the message was successful, false otherwise.
     */
    public suspend fun sendMessage(
        targetUid: String,
        message: Message<ByteArray?>
    ): Boolean
}
