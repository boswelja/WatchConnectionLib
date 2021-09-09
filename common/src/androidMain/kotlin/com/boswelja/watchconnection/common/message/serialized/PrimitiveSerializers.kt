package com.boswelja.watchconnection.common.message.serialized

import java.nio.ByteBuffer

/**
 * A [MessageSerializer] for [String]s.
 */
public class StringSerializer(
    messagePaths: Set<String>
) : MessageSerializer<String>(messagePaths) {
    override suspend fun deserialize(bytes: ByteArray): String = String(bytes, Charsets.UTF_8)
    override suspend fun serialize(data: String): ByteArray = data.toByteArray(Charsets.UTF_8)
}

/**
 * A [MessageSerializer] for [Int]s.
 */
public class IntSerializer(
    messagePaths: Set<String>
) : MessageSerializer<Int>(messagePaths) {
    override suspend fun deserialize(bytes: ByteArray): Int = ByteBuffer.wrap(bytes).int
    override suspend fun serialize(data: Int): ByteArray =
        ByteBuffer.allocate(Int.SIZE_BYTES).putInt(data).array()
}

/**
 * A [MessageSerializer] for [Long]s.
 */
public class LongSerializer(
    messagePaths: Set<String>
) : MessageSerializer<Long>(messagePaths) {
    override suspend fun deserialize(bytes: ByteArray): Long = ByteBuffer.wrap(bytes).long
    override suspend fun serialize(data: Long): ByteArray =
        ByteBuffer.allocate(Long.SIZE_BYTES).putLong(data).array()
}

/**
 * A [MessageSerializer] for [Boolean]s.
 */
public class BooleanSerializer(
    messagePaths: Set<String>
) : MessageSerializer<Boolean>(messagePaths) {
    override suspend fun deserialize(bytes: ByteArray): Boolean = bytes[0].toInt() == 1
    override suspend fun serialize(data: Boolean): ByteArray = byteArrayOf(if (data) 1 else 0)
}

/**
 * A [MessageSerializer] for [Float].
 */
public class FloatSerializer(
    messagePaths: Set<String>
) : MessageSerializer<Float>(messagePaths) {
    override suspend fun deserialize(bytes: ByteArray): Float = ByteBuffer.wrap(bytes).float
    override suspend fun serialize(data: Float): ByteArray =
        ByteBuffer.allocate(Float.SIZE_BYTES).putFloat(data).array()
}

/**
 * A [MessageSerializer] for [Double].
 */
public class DoubleSerializer(
    messagePaths: Set<String>
) : MessageSerializer<Double>(messagePaths) {
    override suspend fun deserialize(bytes: ByteArray): Double = ByteBuffer.wrap(bytes).double
    override suspend fun serialize(data: Double): ByteArray =
        ByteBuffer.allocate(Double.SIZE_BYTES).putDouble(data).array()
}
