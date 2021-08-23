package com.boswelja.watchconnection.core.message.serialized

import kotlinx.coroutines.runBlocking
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class PrimitiveSerializersTest {

    @Test
    fun stringSerializer(): Unit = runBlocking {
        val data = "data"
        val serializer = StringSerializer(setOf())
        val bytes = serializer.serialize(data)
        val deserializedData = serializer.deserialize(bytes)
        expectThat(deserializedData).isEqualTo(data)
    }

    @Test
    fun intSerializer(): Unit = runBlocking {
        val data = 123
        val serializer = IntSerializer(setOf())
        val bytes = serializer.serialize(data)
        val deserializedData = serializer.deserialize(bytes)
        expectThat(deserializedData).isEqualTo(data)
    }

    @Test
    fun longSerializer(): Unit = runBlocking {
        val data = 456L
        val serializer = LongSerializer(setOf())
        val bytes = serializer.serialize(data)
        val deserializedData = serializer.deserialize(bytes)
        expectThat(deserializedData).isEqualTo(data)
    }

    @Test
    fun booleanSerializer(): Unit = runBlocking {
        val data = true
        val serializer = BooleanSerializer(setOf())
        val bytes = serializer.serialize(data)
        val deserializedData = serializer.deserialize(bytes)
        expectThat(deserializedData).isEqualTo(data)
    }

    @Test
    fun floatSerializer(): Unit = runBlocking {
        val data = 0.1f
        val serializer = FloatSerializer(setOf())
        val bytes = serializer.serialize(data)
        val deserializedData = serializer.deserialize(bytes)
        expectThat(deserializedData).isEqualTo(data)
    }

    @Test
    fun doubleSerializer(): Unit = runBlocking {
        val data = 0.1
        val serializer = DoubleSerializer(setOf())
        val bytes = serializer.serialize(data)
        val deserializedData = serializer.deserialize(bytes)
        expectThat(deserializedData).isEqualTo(data)
    }
}
