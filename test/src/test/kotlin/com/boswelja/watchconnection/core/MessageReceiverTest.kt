package com.boswelja.watchconnection.core

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.boswelja.watchconnection.core.MessageReceiver.Companion.EXTRA_DATA
import com.boswelja.watchconnection.core.MessageReceiver.Companion.EXTRA_MESSAGE
import com.boswelja.watchconnection.core.MessageReceiver.Companion.EXTRA_WATCH_ID
import com.boswelja.watchconnection.core.Messages.ACTION_MESSAGE_RECEIVED
import io.mockk.coVerify
import io.mockk.spyk
import io.mockk.verify
import java.util.UUID
import kotlin.random.Random
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.R])
class MessageReceiverTest {

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private lateinit var context: Context
    private lateinit var messageReceiver: MessageReceiver

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        messageReceiver = spyk(ConcreteMessageReceiver())
    }

    @Test
    fun `onReceive does nothing for incorrect actions`() {
        val intent = Intent("action")
        messageReceiver.onReceive(context, intent)
        verify(inverse = true, timeout = TIMEOUT) { messageReceiver.goAsync() }
        coVerify(inverse = true, timeout = TIMEOUT) {
            messageReceiver.onMessageReceived(any(), any())
        }
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

        val expectedMessage = Message(id, message, data)
        coVerify(timeout = TIMEOUT) { messageReceiver.onMessageReceived(context, expectedMessage) }
    }

    companion object {
        private const val TIMEOUT = 100L
    }
}
