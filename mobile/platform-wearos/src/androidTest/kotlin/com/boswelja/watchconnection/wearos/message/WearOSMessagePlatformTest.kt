package com.boswelja.watchconnection.wearos.message

import android.content.Context
import app.cash.turbine.test
import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.common.message.Message
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageOptions
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

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

    @Test
    public fun `sendMessage passes high priority requests to MessageClient`() {
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
    public fun `sendMessage passes low priority requests to MessageClient`() {
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

    @ExperimentalCoroutinesApi
    @Test
    public fun `incomingMessages gets all messages received by OnMessageReceivedListener`(): Unit = runBlocking {
        val messageCount = 10
        val messages = createMessagesFor(messageCount, messagePlatform.platformIdentifier)

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
