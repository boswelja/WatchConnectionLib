package com.boswelja.watchconnection.wearos.message

import android.content.Context
import com.boswelja.watchconnection.common.message.Message
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.google.android.gms.wearable.MessageOptions
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private const val TIMEOUT = 250L

public class WearOSMessagePlatformTest {

    private lateinit var context: Context
    private lateinit var messageClient: DummyMessageClient
    private lateinit var messagePlatform: WearOSMessagePlatform

    @BeforeEach
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
    public fun `incomingMessages gets all messages received by OnMessageReceivedListener`() {
        val messageCount = 10
        val messages = createMessagesFor(messageCount, messagePlatform.platformIdentifier)

        val scope = TestCoroutineScope()

        // Start collecting messages
        val job = Job()
        val collectedMessages = mutableListOf<ReceivedMessage<ByteArray?>>()
        scope.launch(job) {
            messagePlatform.incomingMessages().take(messageCount).collect {
                collectedMessages.add(it)
            }
        }

        // Send the dummy messages
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
        }

        // Make sure we got all the messages
        assertEquals(messages, collectedMessages)
    }
}
