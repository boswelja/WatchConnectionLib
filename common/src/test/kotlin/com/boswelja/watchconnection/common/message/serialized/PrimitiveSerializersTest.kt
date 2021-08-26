package com.boswelja.watchconnection.common.message.serialized

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class PrimitiveSerializersTest {

    @Test
    fun stringSerializer(): Unit = runBlocking {
        val data = "data"
        val serializer = StringSerializer(setOf())
        val bytes = serializer.serialize(data)
        val deserializedData = serializer.deserialize(bytes)
        Assert.assertEquals(data, deserializedData)
    }

    @Test
    fun intSerializer(): Unit = runBlocking {
        val data = 123
        val serializer = IntSerializer(setOf())
        val bytes = serializer.serialize(data)
        val deserializedData = serializer.deserialize(bytes)
        Assert.assertEquals(data, deserializedData)
    }

    @Test
    fun longSerializer(): Unit = runBlocking {
        val data = 456L
        val serializer = LongSerializer(setOf())
        val bytes = serializer.serialize(data)
        val deserializedData = serializer.deserialize(bytes)
        Assert.assertEquals(data, deserializedData)
    }

    @Test
    fun booleanSerializer(): Unit = runBlocking {
        val data = true
        val serializer = BooleanSerializer(setOf())
        val bytes = serializer.serialize(data)
        val deserializedData = serializer.deserialize(bytes)
        Assert.assertEquals(data, deserializedData)
    }

    @Test
    fun floatSerializer(): Unit = runBlocking {
        val data = 0.1f
        val serializer = FloatSerializer(setOf())
        val bytes = serializer.serialize(data)
        val deserializedData = serializer.deserialize(bytes)
        Assert.assertEquals(data, deserializedData)
    }

    @Test
    fun doubleSerializer(): Unit = runBlocking {
        val data = 0.1
        val serializer = DoubleSerializer(setOf())
        val bytes = serializer.serialize(data)
        val deserializedData = serializer.deserialize(bytes)
        Assert.assertEquals(data, deserializedData, 0.00001)
    }
}
