package com.boswelja.watchconnection.common.message.serialized

import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import com.boswelja.watchconnection.common.message.ReceivedMessage
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.R])
class TypedMessageReceiverTest {

    private lateinit var receiver: ConcreteMessageReceiver

    @BeforeEach
    fun setUp() {
        receiver = ConcreteMessageReceiver()
    }

    @Test
    fun `receiver ignores messages with unsupported paths`(): Unit = runBlocking {
        receiver.onMessageReceived(
            InstrumentationRegistry.getInstrumentation().targetContext,
            ReceivedMessage(
                UUID.randomUUID(),
                "invalid-path",
                "data".toByteArray()
            )
        )

        assertEquals(0, receiver.receivedMessages.count())
    }

    @Test
    fun `receiver throws exception when expected data is missing`() {
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                receiver.onMessageReceived(
                    InstrumentationRegistry.getInstrumentation().targetContext,
                    ReceivedMessage(
                        UUID.randomUUID(),
                        MessagePath,
                        null
                    )
                )
            }
        }
    }

    @Test
    fun `receiver deserializes data correctly`(): Unit = runBlocking {
        val testString = "this is a test string"

        // Emulate serialized data received
        receiver.onMessageReceived(
            InstrumentationRegistry.getInstrumentation().targetContext,
            ReceivedMessage(
                UUID.randomUUID(),
                MessagePath,
                testString.toByteArray()
            )
        )

        // Check data was deserialized correctly
        val message = receiver.receivedMessages.firstOrNull()
        assertNotNull(message)
        assertEquals(testString, message!!.data.data)
    }
}
