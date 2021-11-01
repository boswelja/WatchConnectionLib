package com.boswelja.watchconnection.serialization

public expect class ShortArraySerializer(messagePaths: Set<String>) : MessageSerializer<ShortArray>

public expect class IntArraySerializer(messagePaths: Set<String>) : MessageSerializer<IntArray>

public expect class LongArraySerializer(messagePaths: Set<String>) : MessageSerializer<LongArray>

public expect class FloatArraySerializer(messagePaths: Set<String>) : MessageSerializer<FloatArray>

public expect class DoubleArraySerializer(
    messagePaths: Set<String>
) : MessageSerializer<DoubleArray>

public class BooleanArraySerializer(
    override val messagePaths: Set<String>
) : MessageSerializer<BooleanArray> {
    override suspend fun serialize(data: BooleanArray): ByteArray =
        data.map<Byte> { if (it) 1 else 0 }.toByteArray()
    override suspend fun deserialize(bytes: ByteArray?): BooleanArray =
        bytes!!.map { it == (1).toByte() }.toBooleanArray()
}
