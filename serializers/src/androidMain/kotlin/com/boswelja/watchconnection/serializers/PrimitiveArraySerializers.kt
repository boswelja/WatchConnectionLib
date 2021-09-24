package com.boswelja.watchconnection.serializers

import com.boswelja.watchconnection.common.message.MessageSerializer
import java.nio.ByteBuffer

public actual class ShortArraySerializer actual constructor(
    messagePaths: Set<String>
) : MessageSerializer<ShortArray>(messagePaths) {
    override suspend fun serialize(data: ShortArray): ByteArray {
        val buffer = ByteBuffer.allocate(Short.SIZE_BYTES * data.size)
        data.forEach { short ->
            buffer.putShort(short)
        }
        return buffer.array()
    }

    override suspend fun deserialize(bytes: ByteArray): ShortArray =
        ByteBuffer.wrap(bytes).asShortBuffer().array()
}

public actual class IntArraySerializer actual constructor(
    messagePaths: Set<String>
) : MessageSerializer<IntArray>(messagePaths) {
    override suspend fun serialize(data: IntArray): ByteArray {
        val buffer = ByteBuffer.allocate(Int.SIZE_BYTES * data.size)
        data.forEach { int ->
            buffer.putInt(int)
        }
        return buffer.array()
    }

    override suspend fun deserialize(bytes: ByteArray): IntArray =
        ByteBuffer.wrap(bytes).asIntBuffer().array()
}

public actual class LongArraySerializer actual constructor(
    messagePaths: Set<String>
) : MessageSerializer<LongArray>(messagePaths) {
    override suspend fun serialize(data: LongArray): ByteArray {
        val buffer = ByteBuffer.allocate(Long.SIZE_BYTES * data.size)
        data.forEach { long ->
            buffer.putLong(long)
        }
        return buffer.array()
    }

    override suspend fun deserialize(bytes: ByteArray): LongArray =
        ByteBuffer.wrap(bytes).asLongBuffer().array()
}

public actual class FloatArraySerializer actual constructor(
    messagePaths: Set<String>
) : MessageSerializer<FloatArray>(messagePaths) {
    override suspend fun serialize(data: FloatArray): ByteArray {
        val buffer = ByteBuffer.allocate(Short.SIZE_BYTES * data.size)
        data.forEach { float ->
            buffer.putFloat(float)
        }
        return buffer.array()
    }

    override suspend fun deserialize(bytes: ByteArray): FloatArray =
        ByteBuffer.wrap(bytes).asFloatBuffer().array()
}

public actual class DoubleArraySerializer actual constructor(
    messagePaths: Set<String>
) : MessageSerializer<DoubleArray>(messagePaths) {
    override suspend fun serialize(data: DoubleArray): ByteArray {
        val buffer = ByteBuffer.allocate(Double.SIZE_BYTES * data.size)
        data.forEach { double ->
            buffer.putDouble(double)
        }
        return buffer.array()
    }

    override suspend fun deserialize(bytes: ByteArray): DoubleArray =
        ByteBuffer.wrap(bytes).asDoubleBuffer().array()
}
