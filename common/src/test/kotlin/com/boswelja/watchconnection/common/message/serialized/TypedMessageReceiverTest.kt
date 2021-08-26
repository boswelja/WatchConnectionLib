package com.boswelja.watchconnection.common.message.serialized

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.boswelja.watchconnection.common.message.ReceivedMessage
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.R])
class TypedMessageReceiverTest {

    private lateinit var receiver: ConcreteMessageReceiver

    @Before
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

        Assert.assertEquals(0, receiver.receivedMessages.count())
    }

    @Test
    fun `receiver throws exception when expected data is missing`() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
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
        Assert.assertNotNull(message)
        Assert.assertEquals(testString, message!!.data.data)
    }
}
