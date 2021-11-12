package com.boswelja.watchconnection.wearos.message

import android.content.Context
import app.cash.turbine.test
import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.common.message.Message
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageOptions
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val TIMEOUT = 1000L

public class WearOSMessagePlatformTest {

    private lateinit var context: Context
    private lateinit var messageClient: MessageClient
    private lateinit var messagePlatform: WearOSMessagePlatform

    @Before
    public fun setUp() {
        context = mockk()
        messageClient = mockk {
            every { sendMessage(any(), any(), any(), any()) } returns Tasks.forResult(1)
        }
        messagePlatform = WearOSMessagePlatform(messageClient)
    }

    @After
    public fun tearDown() {
        unmockkAll()
    }

    @Test
    public fun sendMessage_sendsMessageWithData() {
        val message = "message"
        val watchId = "watchId"
        val bytes = byteArrayOf(1, 2, 3)

        // Send the message
        val result = runBlocking {
            withTimeout(TIMEOUT) {
                messagePlatform.sendMessage(watchId, message, bytes)
            }
        }

        // Verify the call was made
        verify {
            messageClient.sendMessage(
                watchId,
                message,
                bytes,
                any()
            )
        }
        assertTrue(result)
    }

    @Test
    public fun sendMessage_sendsMessageWithoutData() {
        val message = "message"
        val watchId = "watchId"
        val bytes = null

        // Send the message
        val result = runBlocking {
            withTimeout(TIMEOUT) {
                messagePlatform.sendMessage(watchId, message, bytes)
            }
        }

        // Verify the call was made
        verify {
            messageClient.sendMessage(
                watchId,
                message,
                bytes,
                any()
            )
        }
        assertTrue(result)
    }

    @Test
    public fun sendMessage_respectsHighPriority() {
        val message = "message"
        val watchId = "watchId"

        // Send the message
        runBlocking {
            withTimeout(TIMEOUT) {
                messagePlatform.sendMessage(watchId, message, priority = Message.Priority.HIGH)
            }
        }

        // Verify the call was made
        verify {
            messageClient.sendMessage(
                watchId,
                message,
                null,
                match { it.priority == MessageOptions.MESSAGE_PRIORITY_HIGH }
            )
        }
    }

    @Test
    public fun sendMessage_respectsLowPriority() {
        val message = "message"
        val watchId = "watchId"

        // Send the message
        runBlocking {
            withTimeout(TIMEOUT) {
                messagePlatform.sendMessage(watchId, message, priority = Message.Priority.LOW)
            }
        }

        // Verify the call was made
        verify {
            messageClient.sendMessage(
                watchId,
                message,
                null,
                match { it.priority == MessageOptions.MESSAGE_PRIORITY_LOW }
            )
        }
    }

    @Test
    public fun sendMessage_returnsFalseOnFailure() {
        val message = "message"
        val watchId = "watchId"

        // Set up for failure
        every {
            messageClient.sendMessage(any(), any(), any(), any())
        } throws ApiException(Status.RESULT_INTERNAL_ERROR)

        // Make the call
        val result = runBlocking {
            withTimeout(TIMEOUT) {
                messagePlatform.sendMessage(watchId, message)
            }
        }

        // Check the result
        assertFalse(result)
    }

    @Test
    public fun incomingMessages_flowsAllReceivedMessages(): Unit = runBlocking {
        val messages = createMessagesFor(25, messagePlatform.platformIdentifier)

        var listener: MessageClient.OnMessageReceivedListener? = null
        every { messageClient.removeListener(any()) } returns Tasks.forResult(true)
        every { messageClient.addListener(any()) } answers {
            listener = firstArg()
            Tasks.forResult(null)
        }

        // Start collecting messages
        messagePlatform.incomingMessages().test(TIMEOUT) {
            messages.forEach { message ->
                listener!!.onMessageReceived(
                    DummyMessageEvent(
                        Watch.getInfoFromUid(message.sourceUid).second,
                        message.path,
                        message.data
                    )
                )
                assertEquals(message, awaitItem())
            }
        }
    }
}
