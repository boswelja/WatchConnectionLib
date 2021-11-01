package com.boswelja.watchconnection.serialization

public class ByteSerializer(
    override val messagePaths: Set<String>
) : MessageSerializer<Byte> {
    override suspend fun serialize(data: Byte): ByteArray = byteArrayOf(data)
    override suspend fun deserialize(bytes: ByteArray?): Byte = bytes!!.first()
}

public expect class ShortSerializer(messagePaths: Set<String>) : MessageSerializer<Short>

public expect class IntSerializer(messagePaths: Set<String>) : MessageSerializer<Int>

public expect class LongSerializer(messagePaths: Set<String>) : MessageSerializer<Long>

public expect class FloatSerializer(messagePaths: Set<String>) : MessageSerializer<Float>

public expect class DoubleSerializer(messagePaths: Set<String>) : MessageSerializer<Double>

public class BooleanSerializer(
    override val messagePaths: Set<String>
) : MessageSerializer<Boolean> {
    override suspend fun serialize(data: Boolean): ByteArray = byteArrayOf(if (data) 1 else 0)
    override suspend fun deserialize(bytes: ByteArray?): Boolean = bytes!!.first() == (1).toByte()
}

public expect class CharSerializer(messagePaths: Set<String>) : MessageSerializer<Char>

public expect class StringSerializer(messagePaths: Set<String>) : MessageSerializer<String>
