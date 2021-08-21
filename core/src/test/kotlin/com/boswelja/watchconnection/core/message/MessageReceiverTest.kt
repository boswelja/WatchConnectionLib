package com.boswelja.watchconnection.core.message

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.boswelja.watchconnection.core.message.MessageReceiver.Companion.EXTRA_DATA
import com.boswelja.watchconnection.core.message.MessageReceiver.Companion.EXTRA_MESSAGE
import com.boswelja.watchconnection.core.message.MessageReceiver.Companion.EXTRA_WATCH_ID
import com.boswelja.watchconnection.core.message.Messages.ACTION_MESSAGE_RECEIVED
import java.util.UUID
import kotlin.random.Random
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.isEmpty

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.R])
class MessageReceiverTest {

    private lateinit var context: Context
    private lateinit var messageReceiver: ConcreteMessageReceiver

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        messageReceiver = ConcreteMessageReceiver()
    }

    @Test
    fun `onReceive does nothing for incorrect actions`() {
        // Create an Intent with no data
        val intent = Intent("action")
        messageReceiver.onReceive(context, intent)
        expectThat(messageReceiver.receivedMessages).isEmpty()
    }

    @Test
    fun `onReceive passes variables to onMessageReceived`() {
        val id = UUID.randomUUID()
        val message = "message"
        val data = Random.nextBytes(10)

        Intent(ACTION_MESSAGE_RECEIVED).apply {
            putExtra(EXTRA_WATCH_ID, id.toString())
            putExtra(EXTRA_MESSAGE, message)
            putExtra(EXTRA_DATA, data)
        }.also { intent ->
            messageReceiver.onReceive(context, intent)
        }

        val expectedMessage = ReceivedMessage(id, message, data)
        expectThat(messageReceiver.receivedMessages).contains(expectedMessage)
    }
}
