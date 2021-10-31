package com.boswelja.watchconnection.serialization

import com.boswelja.watchconnection.common.message.MessageSerializer
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

public class PrimitiveArraySerializersTest {

    @Test
    public fun shortArraySerializer() {
        testSerializer(
            ShortArraySerializer(setOf()),
            shortArrayOf(1, 2, 4)
        )
    }

    @Test
    public fun intArraySerializer() {
        testSerializer(
            IntArraySerializer(setOf()),
            intArrayOf(10, 20, 30)
        )
    }

    @Test
    public fun longArraySerializer() {
        testSerializer(
            LongArraySerializer(setOf()),
            longArrayOf(1000, 2000, 3000)
        )
    }

    @Test
    public fun floatArraySerializer() {
        testSerializer(
            FloatArraySerializer(setOf()),
            floatArrayOf(0.1f, 1.0f, 2.5f)
        )
    }

    @Test
    public fun doubleArraySerializer() {
        testSerializer(
            DoubleArraySerializer(setOf()),
            doubleArrayOf(0.1, 1.0, 2.5)
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
