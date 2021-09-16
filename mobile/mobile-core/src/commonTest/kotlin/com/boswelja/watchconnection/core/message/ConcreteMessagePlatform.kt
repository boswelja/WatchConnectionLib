package com.boswelja.watchconnection.core.message

import com.boswelja.watchconnection.common.message.Message
import com.boswelja.watchconnection.common.message.ReceivedMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class ConcreteMessagePlatform(
    platformId: String
) : MessagePlatform() {

    val incomingMessages = MutableSharedFlow<ReceivedMessage<ByteArray?>>()

    val sentMessages = mutableListOf<ReceivedMessage<ByteArray?>>()

    override val platformIdentifier: String = platformId

    override fun incomingMessages(): Flow<ReceivedMessage<ByteArray?>> = incomingMessages

    override suspend fun sendMessage(
        watchId: String,
        message: String,
        data: ByteArray?,
        priority: Message.Priority
    ): Boolean {
        sentMessages.add(
            ReceivedMessage(watchId, message, data)
        )
        return true
    }
}

fun createPlatforms(count: Int): List<ConcreteMessagePlatform> {
    return (0 until count).map {
        ConcreteMessagePlatform("platform$it")
    }
}
