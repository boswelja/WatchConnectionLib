package com.boswelja.watchconnection.core.message.serialized

import java.math.BigInteger

/**
 * A [MessageSerializer] for [String]s.
 */
class StringSerializer(
    messagePaths: Set<String>
) : MessageSerializer<String>(messagePaths) {
    override suspend fun deserialize(bytes: ByteArray): String = String(bytes, Charsets.UTF_8)
    override suspend fun serialize(data: String): ByteArray = data.toByteArray(Charsets.UTF_8)
}

/**
 * A [MessageSerializer] for [Int]s.
 */
class IntSerializer(
    messagePaths: Set<String>
) : MessageSerializer<Int>(messagePaths) {
    override suspend fun deserialize(bytes: ByteArray): Int = BigInteger(bytes).toInt()
    override suspend fun serialize(data: Int): ByteArray = data.toBigInteger().toByteArray()
}

/**
 * A [MessageSerializer] for [Long]s.
 */
class LongSerializer(
    messagePaths: Set<String>
) : MessageSerializer<Long>(messagePaths) {
    override suspend fun deserialize(bytes: ByteArray): Long = BigInteger(bytes).toLong()
    override suspend fun serialize(data: Long): ByteArray = data.toBigInteger().toByteArray()
}

/**
 * A [MessageSerializer] for [Boolean]s.
 */
class BooleanSerializer(
    messagePaths: Set<String>
) : MessageSerializer<Boolean>(messagePaths) {
    override suspend fun deserialize(bytes: ByteArray): Boolean = bytes[0].toInt() == 1
    override suspend fun serialize(data: Boolean): ByteArray = byteArrayOf(if (data) 1 else 0)
}
