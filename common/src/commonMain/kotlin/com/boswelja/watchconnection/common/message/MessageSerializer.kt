package com.boswelja.watchconnection.common.message

/**
 * A generic serializer implementation, designed to serialize/deserialize classes with some
 * serialization standard.
 * @param messagePaths A [Set] of [Message.path]s this serializer supports.
 */
@Deprecated("Use serialization implementation")
public abstract class MessageSerializer<T>(
    public val messagePaths: Set<String>
) {

    /**
     * Serialize a class.
     * @param data The class [T] to serialize.
     * @return The [ByteArray] of serialized data.
     */
    public abstract suspend fun serialize(data: T): ByteArray

    /**
     * Deserialize a class.
     * @param bytes The [ByteArray] to deserialize.
     * @return The deserialized class [T].
     */
    public abstract suspend fun deserialize(bytes: ByteArray?): T

    /**
     * This exists to effectively allow serializing [T] after type erasure.
     */
    @Suppress("UNCHECKED_CAST")
    public suspend fun serializeAny(data: Any): ByteArray {
        return serialize(data as T)
    }
}
