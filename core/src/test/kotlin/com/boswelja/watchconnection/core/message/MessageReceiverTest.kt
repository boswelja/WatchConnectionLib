package com.boswelja.watchconnection.core.message

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.boswelja.watchconnection.core.message.MessageReceiver.Companion.EXTRA_DATA
import com.boswelja.watchconnection.core.message.MessageReceiver.Companion.EXTRA_MESSAGE
import com.boswelja.watchconnection.core.message.MessageReceiver.Companion.EXTRA_WATCH_ID
import com.boswelja.watchconnection.core.message.Messages.ACTION_MESSAGE_RECEIVED
import io.mockk.mockk
import java.util.UUID
import kotlin.random.Random
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNull

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.R])
class MessageReceiverTest {

    private lateinit var context: Context
    private lateinit var messageReceiver: ConcreteMessageReceiver

    @Before
    fun setUp() {
        context = mockk()
        messageReceiver = ConcreteMessageReceiver()
    }

    @Test
    fun `onReceive does nothing for incorrect actions`(): Unit = runBlocking {
        // Create an Intent with no data
        val intent = Intent("action")
        messageReceiver.onReceive(context, intent)
        val message = withTimeoutOrNull(TIMEOUT) {
            messageReceiver.receivedMessage.mapNotNull { it }.firstOrNull()
        }
        expectThat(message).isNull()
    }

    @Test
    fun `onReceive passes variables to onMessageReceived`(): Unit = runBlocking {
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

        val receivedMessage = withTimeoutOrNull(TIMEOUT) {
            messageReceiver.receivedMessage.mapNotNull { it }.firstOrNull()
        }
        val expectedMessage = Message(id, message, data)
        expectThat(receivedMessage).isEqualTo(expectedMessage)
    }

    companion object {
        private const val TIMEOUT = 250L
    }
}
