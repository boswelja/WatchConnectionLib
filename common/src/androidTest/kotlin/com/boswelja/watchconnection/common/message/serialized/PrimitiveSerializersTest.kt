package com.boswelja.watchconnection.common.message.serialized

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

public class PrimitiveSerializersTest {

    @Test
    public fun stringSerializer(): Unit = runBlocking {
        val data = "data"
        val serializer = StringSerializer(setOf())
        val bytes = serializer.serialize(data)
        val deserializedData = serializer.deserialize(bytes)
        assertEquals(data, deserializedData)
    }

    @Test
    public fun intSerializer(): Unit = runBlocking {
        val data = 123
        val serializer = IntSerializer(setOf())
        val bytes = serializer.serialize(data)
        val deserializedData = serializer.deserialize(bytes)
        assertEquals(data, deserializedData)
    }

    @Test
    public fun longSerializer(): Unit = runBlocking {
        val data = 456L
        val serializer = LongSerializer(setOf())
        val bytes = serializer.serialize(data)
        val deserializedData = serializer.deserialize(bytes)
        assertEquals(data, deserializedData)
    }

    @Test
    public fun booleanSerializer(): Unit = runBlocking {
        val data = true
        val serializer = BooleanSerializer(setOf())
        val bytes = serializer.serialize(data)
        val deserializedData = serializer.deserialize(bytes)
        assertEquals(data, deserializedData)
    }

    @Test
    public fun floatSerializer(): Unit = runBlocking {
        val data = 0.1f
        val serializer = FloatSerializer(setOf())
        val bytes = serializer.serialize(data)
        val deserializedData = serializer.deserialize(bytes)
        assertEquals(data, deserializedData)
    }

    @Test
    public fun doubleSerializer(): Unit = runBlocking {
        val data = 0.1
        val serializer = DoubleSerializer(setOf())
        val bytes = serializer.serialize(data)
        val deserializedData = serializer.deserialize(bytes)
        assertEquals(data, deserializedData, 0.00001)
    }
}
