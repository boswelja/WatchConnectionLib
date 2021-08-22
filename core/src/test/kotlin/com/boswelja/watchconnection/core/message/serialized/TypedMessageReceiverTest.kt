package com.boswelja.watchconnection.core.message.serialized

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.boswelja.watchconnection.core.message.ReceivedMessage
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull

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

        expectThat(receiver.receivedMessages).isEmpty()
    }

    @Test
    fun `receiver throws exception when expected data is missing`(): Unit = runBlocking {
        expectThrows<IllegalArgumentException> {
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
        expectThat(receiver.receivedMessages.firstOrNull())
            .isNotNull()
            .get("data") { data.data }
            .isEqualTo(testString)
    }
}
