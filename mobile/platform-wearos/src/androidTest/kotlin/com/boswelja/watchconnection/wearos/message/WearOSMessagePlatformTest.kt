package com.boswelja.watchconnection.wearos.message

import android.content.Context
import app.cash.turbine.test
import com.boswelja.watchconnection.common.message.Message
import com.google.android.gms.wearable.MessageOptions
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

private const val TIMEOUT = 1000L

public class WearOSMessagePlatformTest {

    private lateinit var context: Context
    private lateinit var messageClient: DummyMessageClient
    private lateinit var messagePlatform: WearOSMessagePlatform

    @Before
    public fun setUp() {
        context = mockk()
        messageClient = DummyMessageClient(context)
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

        // Start collecting messages
        messagePlatform.incomingMessages().take(messageCount).test(TIMEOUT) {
            messages.forEach { message ->
                messageClient.listeners.forEach { listener ->
                    listener.onMessageReceived(
                        DummyMessageEvent(
                            message.sourceUid,
                            message.path,
                            message.data
                        )
                    )
                }
                assertEquals(message, awaitItem())
            }
        }
    }
}
