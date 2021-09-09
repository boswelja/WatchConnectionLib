package com.boswelja.watchconnection.common.message

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import com.boswelja.watchconnection.common.message.MessageReceiver.Companion.ACTION_MESSAGE_RECEIVED
import com.boswelja.watchconnection.common.message.MessageReceiver.Companion.EXTRA_DATA
import com.boswelja.watchconnection.common.message.MessageReceiver.Companion.EXTRA_MESSAGE
import com.boswelja.watchconnection.common.message.MessageReceiver.Companion.EXTRA_WATCH_ID
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.R])
class MessageReceiverTest {

    private val receiverFinished = MutableStateFlow(false)

    private lateinit var context: Context
    private lateinit var messageReceiver: ConcreteMessageReceiver

    @BeforeEach
    fun setUp() {
        runBlocking { receiverFinished.emit(true) }
        val pendingResult = mockk<BroadcastReceiver.PendingResult>()
        every { pendingResult.finish() } answers {
            runBlocking { receiverFinished.emit(true) }
        }

        context = InstrumentationRegistry.getInstrumentation().targetContext
        messageReceiver = spyk(ConcreteMessageReceiver())
        every { messageReceiver.goAsync() } answers {
            runBlocking { receiverFinished.emit(false) }
            pendingResult
        }
    }

    @Test
    fun `onReceive does nothing for incorrect actions`() {
        // Create an Intent with no data
        val intent = Intent("action")
        messageReceiver.onReceive(context, intent)

        runBlocking {
            withTimeout(2000) {
                receiverFinished.first { it }
            }
        }

        assertEquals(0, messageReceiver.receivedMessages.count())
    }

    @Test
    fun `onReceive passes variables to onMessageReceived`() {
        val id = "uid"
        val message = "message"
        val data = Random.nextBytes(10)

        Intent(ACTION_MESSAGE_RECEIVED).apply {
            putExtra(EXTRA_WATCH_ID, id)
            putExtra(EXTRA_MESSAGE, message)
            putExtra(EXTRA_DATA, data)
        }.also { intent ->
            messageReceiver.onReceive(context, intent)
        }

        runBlocking {
            withTimeout(2000) {
                receiverFinished.first { it }
            }
        }

        val expectedMessage = ReceivedMessage(id, message, data)
        assertEquals(expectedMessage, messageReceiver.receivedMessages.first())
    }
}
