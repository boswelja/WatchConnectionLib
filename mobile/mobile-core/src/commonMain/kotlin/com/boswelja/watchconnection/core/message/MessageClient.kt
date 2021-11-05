package com.boswelja.watchconnection.core.message

import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.common.message.Message
import com.boswelja.watchconnection.common.message.MessageClient
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.boswelja.watchconnection.core.BaseClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge

/**
 * MessageClient takes a number of [MessagePlatform]s, and provides a common interface between them.
 * @param platforms The [MessagePlatform]s this MessageClient should support.
 */
public class MessageClient(
    platforms: List<MessagePlatform>
) : BaseClient<MessagePlatform>(platforms),
    MessageClient {

    /**
     * A [Flow] of [ReceivedMessage]s received by all platforms. Messages collected here have no
     * additional processing performed, and thus only contain the raw data in bytes.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun incomingMessages(): Flow<ReceivedMessage<ByteArray?>> = platforms.values
        .map { it.incomingMessages() }
        .merge()

    /**
     * Send a message to a [Watch]. See [MessagePlatform.sendMessage].
     * @param targetUid The [Watch.uid] to send the message to.
     * @param message The [Message] to send.
     * @return true if sending the message was successful, false otherwise.
     */
    override suspend fun sendMessage(
        targetUid: String,
        message: Message<ByteArray?>
    ): Boolean {
        val (platformId, internalId) = Watch.getInfoFromUid(targetUid)
        val platform = platforms[platformId]
        requireNotNull(platform) { "No platform registered for $platformId" }

        return platform.sendMessage(internalId, message.path, message.data, message.priority)
    }
}
