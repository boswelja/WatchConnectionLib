package com.boswelja.watchconnection.core.message.serialized

import com.boswelja.watchconnection.common.message.serialized.MessageSerializer

const val MessagePath = "message-path"

object ConcreteMessageSerializer : MessageSerializer<ConcreteDataType>(
    messagePaths = setOf(MessagePath)
) {
    override suspend fun serialize(data: ConcreteDataType): ByteArray {
        return data.data.encodeToByteArray()
    }

    override suspend fun deserialize(bytes: ByteArray): ConcreteDataType {
        return ConcreteDataType(bytes.decodeToString())
    }
}
