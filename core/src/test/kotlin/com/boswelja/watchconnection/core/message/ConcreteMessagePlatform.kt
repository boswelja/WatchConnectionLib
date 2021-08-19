package com.boswelja.watchconnection.core.message

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ConcreteMessagePlatform(
    private val incomingMessage: ByteArrayMessage
) : MessagePlatform {

    override val platformIdentifier: String = PLATFORM

    override fun incomingMessages(): Flow<ByteArrayMessage> = flow { emit(incomingMessage) }

    override suspend fun sendMessage(
        watchId: String,
        message: String,
        data: ByteArray?,
        priority: MessagePriority
    ): Boolean {
        return true
    }

    companion object {
        const val PLATFORM = "platform"
    }
}
