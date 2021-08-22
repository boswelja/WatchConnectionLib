package com.boswelja.watchconnection.core.message

import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class ConcreteMessagePlatform(
    platformId: String
) : MessagePlatform {

    val incomingMessages = MutableSharedFlow<ReceivedMessage<ByteArray?>>()

    val sentMessages = mutableListOf<ReceivedMessage<ByteArray?>>()

    override val platformIdentifier: String = platformId

    override fun incomingMessages(): Flow<ReceivedMessage<ByteArray?>> = incomingMessages

    override suspend fun sendMessage(
        watchId: String,
        message: String,
        data: ByteArray?,
        priority: MessagePriority
    ): Boolean {
        sentMessages.add(
            ReceivedMessage(UUID.randomUUID(), message, data)
        )
        return true
    }
}

fun createPlatforms(count: Int): List<ConcreteMessagePlatform> {
    return (0 until count).map {
        ConcreteMessagePlatform("platform$it")
    }
}
