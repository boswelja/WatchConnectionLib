package com.boswelja.watchconnection.serialization

import com.boswelja.watchconnection.common.message.Message
import com.boswelja.watchconnection.common.message.MessageClient
import com.boswelja.watchconnection.common.message.ReceivedMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class DummyMessageClient : MessageClient {

    var messageSendResult: Boolean = true

    var sentMessage: Message<ByteArray?>? = null
        private set

    val incomingMessages = MutableSharedFlow<ReceivedMessage<ByteArray?>>()

    override fun incomingMessages(): Flow<ReceivedMessage<ByteArray?>> = incomingMessages

    override suspend fun sendMessage(
        targetUid: String,
        message: Message<ByteArray?>
    ): Boolean {
        sentMessage = message
        return messageSendResult
    }
}
