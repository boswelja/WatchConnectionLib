package com.boswelja.watchconnection.core.message.serialized

/**
 * A generic serializer implementation, designed to serialize/deserialize classes with some
 * serialization standard.
 */
abstract class DataSerializer<T> {

    /**
     * Serialize a class.
     * @param data The class [T] to serialize.
     * @return The [ByteArray] of serialized data.
     */
    abstract suspend fun serialize(data: T): ByteArray

    /**
     * Deserialize a class.
     * @param bytes The [ByteArray] to deserialize.
     * @return The deserialized class [T].
     */
    abstract suspend fun deserialize(bytes: ByteArray): T
}