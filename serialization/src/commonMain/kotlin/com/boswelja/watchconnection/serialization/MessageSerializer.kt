package com.boswelja.watchconnection.serialization

/**
 * A generic serializer implementation, designed to serialize/deserialize classes with some
 * serialization standard.
 */
public interface MessageSerializer<T> {

    /**
     * The set of message paths this serializer can handle.
     */
    public val messagePaths: Set<String>

    /**
     * Serialize a class.
     * @param data The class [T] to serialize.
     * @return The [ByteArray] of serialized data.
     */
    public suspend fun serialize(data: T): ByteArray

    /**
     * Deserialize a class.
     * @param bytes The [ByteArray] to deserialize.
     * @return The deserialized class [T].
     */
    public suspend fun deserialize(bytes: ByteArray?): T
}
