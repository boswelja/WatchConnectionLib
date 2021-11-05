package com.boswelja.watchconnection.serialization

/**
 * A [MessageSerializer] for handling [Byte].
 */
public class ByteSerializer(
    override val messagePaths: Set<String>
) : MessageSerializer<Byte> {
    override suspend fun serialize(data: Byte): ByteArray = byteArrayOf(data)
    override suspend fun deserialize(bytes: ByteArray?): Byte = bytes!!.first()
}

/**
 * A [MessageSerializer] for handling [Short].
 */
public expect class ShortSerializer(messagePaths: Set<String>) : MessageSerializer<Short>

/**
 * A [MessageSerializer] for handling [Int].
 */
public expect class IntSerializer(messagePaths: Set<String>) : MessageSerializer<Int>

/**
 * A [MessageSerializer] for handling [Long].
 */
public expect class LongSerializer(messagePaths: Set<String>) : MessageSerializer<Long>

/**
 * A [MessageSerializer] for handling [Float].
 */
public expect class FloatSerializer(messagePaths: Set<String>) : MessageSerializer<Float>

/**
 * A [MessageSerializer] for handling [Double].
 */
public expect class DoubleSerializer(messagePaths: Set<String>) : MessageSerializer<Double>

/**
 * A [MessageSerializer] for handling [Boolean].
 */
public class BooleanSerializer(
    override val messagePaths: Set<String>
) : MessageSerializer<Boolean> {
    override suspend fun serialize(data: Boolean): ByteArray = byteArrayOf(if (data) 1 else 0)
    override suspend fun deserialize(bytes: ByteArray?): Boolean = bytes!!.first() == 1.toByte()
}

/**
 * A [MessageSerializer] for handling [Char].
 */
public expect class CharSerializer(messagePaths: Set<String>) : MessageSerializer<Char>

/**
 * A [MessageSerializer] for handling [String].
 */
public expect class StringSerializer(messagePaths: Set<String>) : MessageSerializer<String>
