package com.boswelja.watchconnection.core.message.serialized

const val MessagePath = "message-path"

object ConcreteDataSerializer : DataSerializer<ConcreteDataType>(
    messagePaths = setOf(MessagePath)
) {
    override suspend fun serialize(data: ConcreteDataType): ByteArray {
        return data.data.toByteArray()
    }

    override suspend fun deserialize(bytes: ByteArray): ConcreteDataType {
        return ConcreteDataType(String(bytes))
    }
}
