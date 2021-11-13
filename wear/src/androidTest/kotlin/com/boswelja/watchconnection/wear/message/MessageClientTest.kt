package com.boswelja.watchconnection.wear.message

import app.cash.turbine.test
import com.boswelja.watchconnection.common.message.Message
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.MessageOptions
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import com.google.android.gms.wearable.MessageClient as WearMessageClient

private const val TIMEOUT = 1000L

public class MessageClientTest {

    private lateinit var wearMessageClient: WearMessageClient
    private lateinit var messageClient: MessageClient

    @Before
    public fun setUp() {
        wearMessageClient = mockk {
            every { sendMessage(any(), any(), any(), any()) } returns Tasks.forResult(1)
        }
        messageClient = MessageClient(wearMessageClient)
    }

    @Test
    public fun sendMessage_sendsMessageWithData() {
        val message = "message"
        val watchId = "watchId"
        val bytes = byteArrayOf(1, 2, 3)

        // Send the message
        val result = runBlocking {
            withTimeout(TIMEOUT) {
                messageClient.sendMessage(watchId, Message(message, bytes))
            }
        }

        // Verify the call was made
        verify {
            wearMessageClient.sendMessage(
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
                messageClient.sendMessage(watchId, Message(message, bytes))
            }
        }

        // Verify the call was made
        verify {
            wearMessageClient.sendMessage(
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
                messageClient.sendMessage(
                    watchId,
                    Message(message, null, Message.Priority.HIGH)
                )
            }
        }

        // Verify the call was made
        verify {
            wearMessageClient.sendMessage(
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
                messageClient.sendMessage(
                    watchId,
                    Message(message, null, Message.Priority.LOW)
                )
            }
        }

        // Verify the call was made
        verify {
            wearMessageClient.sendMessage(
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
            wearMessageClient.sendMessage(any(), any(), any(), any())
        } throws ApiException(Status.RESULT_INTERNAL_ERROR)

        // Make the call
        val result = runBlocking {
            withTimeout(TIMEOUT) {
                messageClient.sendMessage(watchId, Message(message, null))
            }
        }

        // Check the result
        assertFalse(result)
    }

    @Test
    public fun incomingMessages_flowsAllReceivedMessages(): Unit = runBlocking {
        val messages = createMessagesFor(25)

        var listener: WearMessageClient.OnMessageReceivedListener? = null
        every { wearMessageClient.removeListener(any()) } returns Tasks.forResult(true)
        every { wearMessageClient.addListener(any()) } answers {
            listener = firstArg()
            Tasks.forResult(null)
        }

        // Start collecting messages
        messageClient.incomingMessages().test {
            messages.forEach { message ->
                listener!!.onMessageReceived(
                    DummyMessageEvent(
                        message.sourceUid,
                        message.path,
                        message.data
                    )
                )
                assertEquals(message, awaitItem())
            }
        }
    }
}
