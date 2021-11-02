package com.boswelja.watchconnection.serialization

@Deprecated("Create your own serializer for more complex data")
public expect class ShortArraySerializer(messagePaths: Set<String>) : MessageSerializer<ShortArray>

@Deprecated("Create your own serializer for more complex data")
public expect class IntArraySerializer(messagePaths: Set<String>) : MessageSerializer<IntArray>

@Deprecated("Create your own serializer for more complex data")
public expect class LongArraySerializer(messagePaths: Set<String>) : MessageSerializer<LongArray>

@Deprecated("Create your own serializer for more complex data")
public expect class FloatArraySerializer(messagePaths: Set<String>) : MessageSerializer<FloatArray>

@Deprecated("Create your own serializer for more complex data")
public expect class DoubleArraySerializer(
    messagePaths: Set<String>
) : MessageSerializer<DoubleArray>

@Deprecated("Create your own serializer for more complex data")
public class BooleanArraySerializer(
    override val messagePaths: Set<String>
) : MessageSerializer<BooleanArray> {
    override suspend fun serialize(data: BooleanArray): ByteArray =
        data.map<Byte> { if (it) 1 else 0 }.toByteArray()
    override suspend fun deserialize(bytes: ByteArray?): BooleanArray =
        bytes!!.map { it == (1).toByte() }.toBooleanArray()
}
