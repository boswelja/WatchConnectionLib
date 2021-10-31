package com.boswelja.watchconnection.serializers

import com.boswelja.watchconnection.common.message.MessageSerializer
import java.nio.ByteBuffer

public actual class ShortSerializer actual constructor(
    messagePaths: Set<String>
) : MessageSerializer<Short>(messagePaths) {
    override suspend fun serialize(data: Short): ByteArray =
        ByteBuffer.allocate(Short.SIZE_BYTES).putShort(data).array()
    override suspend fun deserialize(bytes: ByteArray?): Short = ByteBuffer.wrap(bytes).short
}

public actual class IntSerializer actual constructor(
    messagePaths: Set<String>
) : MessageSerializer<Int>(messagePaths) {
    override suspend fun serialize(data: Int): ByteArray =
        ByteBuffer.allocate(Int.SIZE_BYTES).putInt(data).array()
    override suspend fun deserialize(bytes: ByteArray?): Int = ByteBuffer.wrap(bytes).int
}

public actual class LongSerializer actual constructor(
    messagePaths: Set<String>
) : MessageSerializer<Long>(messagePaths) {
    override suspend fun serialize(data: Long): ByteArray =
        ByteBuffer.allocate(Long.SIZE_BYTES).putLong(data).array()
    override suspend fun deserialize(bytes: ByteArray?): Long = ByteBuffer.wrap(bytes).long
}

public actual class FloatSerializer actual constructor(
    messagePaths: Set<String>
) : MessageSerializer<Float>(messagePaths) {
    override suspend fun serialize(data: Float): ByteArray =
        ByteBuffer.allocate(Float.SIZE_BYTES).putFloat(data).array()
    override suspend fun deserialize(bytes: ByteArray?): Float = ByteBuffer.wrap(bytes).float
}

public actual class DoubleSerializer actual constructor(
    messagePaths: Set<String>
) : MessageSerializer<Double>(messagePaths) {
    override suspend fun serialize(data: Double): ByteArray =
        ByteBuffer.allocate(Double.SIZE_BYTES).putDouble(data).array()

    override suspend fun deserialize(bytes: ByteArray?): Double = ByteBuffer.wrap(bytes).double
}

public actual class CharSerializer actual constructor(
    messagePaths: Set<String>
) : MessageSerializer<Char>(messagePaths) {
    override suspend fun serialize(data: Char): ByteArray =
        ByteBuffer.allocate(Char.SIZE_BYTES).putChar(data).array()
    override suspend fun deserialize(bytes: ByteArray?): Char = ByteBuffer.wrap(bytes).char
}

public actual class StringSerializer actual constructor(
    messagePaths: Set<String>
) : MessageSerializer<String>(messagePaths) {
    override suspend fun serialize(data: String): ByteArray = data.toByteArray(Charsets.UTF_8)
    override suspend fun deserialize(bytes: ByteArray?): String = String(bytes!!, Charsets.UTF_8)
}
