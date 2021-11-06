package com.boswelja.watchconnection.serialization

import java.nio.ByteBuffer

/**
 * A [MessageSerializer] for handling [Short].
 */
public actual class ShortSerializer actual constructor(
    override val messagePaths: Set<String>
) : MessageSerializer<Short> {
    override suspend fun serialize(data: Short): ByteArray =
        ByteBuffer.allocate(Short.SIZE_BYTES).putShort(data).array()
    override suspend fun deserialize(bytes: ByteArray?): Short = ByteBuffer.wrap(bytes!!).short
}

/**
 * A [MessageSerializer] for handling [Int].
 */
public actual class IntSerializer actual constructor(
    override val messagePaths: Set<String>
) : MessageSerializer<Int> {
    override suspend fun serialize(data: Int): ByteArray =
        ByteBuffer.allocate(Int.SIZE_BYTES).putInt(data).array()
    override suspend fun deserialize(bytes: ByteArray?): Int = ByteBuffer.wrap(bytes!!).int
}

/**
 * A [MessageSerializer] for handling [Long].
 */
public actual class LongSerializer actual constructor(
    override val messagePaths: Set<String>
) : MessageSerializer<Long> {
    override suspend fun serialize(data: Long): ByteArray =
        ByteBuffer.allocate(Long.SIZE_BYTES).putLong(data).array()
    override suspend fun deserialize(bytes: ByteArray?): Long = ByteBuffer.wrap(bytes!!).long
}

/**
 * A [MessageSerializer] for handling [Float].
 */
public actual class FloatSerializer actual constructor(
    override val messagePaths: Set<String>
) : MessageSerializer<Float> {
    override suspend fun serialize(data: Float): ByteArray =
        ByteBuffer.allocate(Float.SIZE_BYTES).putFloat(data).array()
    override suspend fun deserialize(bytes: ByteArray?): Float = ByteBuffer.wrap(bytes!!).float
}

/**
 * A [MessageSerializer] for handling [Double].
 */
public actual class DoubleSerializer actual constructor(
    override val messagePaths: Set<String>
) : MessageSerializer<Double> {
    override suspend fun serialize(data: Double): ByteArray =
        ByteBuffer.allocate(Double.SIZE_BYTES).putDouble(data).array()

    override suspend fun deserialize(bytes: ByteArray?): Double = ByteBuffer.wrap(bytes!!).double
}

/**
 * A [MessageSerializer] for handling [Char].
 */
public actual class CharSerializer actual constructor(
    override val messagePaths: Set<String>
) : MessageSerializer<Char> {
    override suspend fun serialize(data: Char): ByteArray =
        ByteBuffer.allocate(Char.SIZE_BYTES).putChar(data).array()
    override suspend fun deserialize(bytes: ByteArray?): Char = ByteBuffer.wrap(bytes!!).char
}

/**
 * A [MessageSerializer] for handling [String].
 */
public actual class StringSerializer actual constructor(
    override val messagePaths: Set<String>
) : MessageSerializer<String> {
    override suspend fun serialize(data: String): ByteArray = data.toByteArray(Charsets.UTF_8)
    override suspend fun deserialize(bytes: ByteArray?): String = String(bytes!!, Charsets.UTF_8)
}
