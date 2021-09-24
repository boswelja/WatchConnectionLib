package com.boswelja.watchconnection.serializers

import com.boswelja.watchconnection.common.message.MessageSerializer
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

public class PrimitiveSerializersTest {

    @Test
    public fun byteSerializer() {
        testSerializer(
            ByteSerializer(setOf()),
            7
        )
    }

    @Test
    public fun shortSerializer() {
        testSerializer(
            ShortSerializer(setOf()),
            16
        )
    }

    @Test
    public fun intSerializer() {
        testSerializer(
            IntSerializer(setOf()),
            120
        )
    }

    @Test
    public fun longSerializer() {
        testSerializer(
            LongSerializer(setOf()),
            5000
        )
    }

    @Test
    public fun floatSerializer() {
        testSerializer(
            FloatSerializer(setOf()),
            1.3f
        )
    }

    @Test
    public fun doubleSerializer() {
        testSerializer(
            DoubleSerializer(setOf()),
            1.5
        )
    }

    @Test
    public fun charSerializer() {
        testSerializer(
            CharSerializer(setOf()),
            'c'
        )
    }

    @Test
    public fun stringSerializer() {
        testSerializer(
            StringSerializer(setOf()),
            "string"
        )
    }

    private fun <T> testSerializer(
        serializer: MessageSerializer<T>,
        testValue: T
    ): Unit = runBlocking {
        val bytes = serializer.serialize(testValue)
        val actual = serializer.deserialize(bytes)
        assertEquals(testValue, actual)
    }
}
