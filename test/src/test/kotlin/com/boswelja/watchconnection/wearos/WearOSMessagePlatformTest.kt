package com.boswelja.watchconnection.wearos

import com.boswelja.watchconnection.core.message.Message
import com.boswelja.watchconnection.createMessagesFor
import com.boswelja.watchconnection.wearos.rules.MessageClientTestRule
import com.google.android.gms.wearable.MessageOptions
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.containsExactlyInAnyOrder

class WearOSMessagePlatformTest {

    @get:Rule
    val messageClientTestRule = MessageClientTestRule()

    private lateinit var messagePlatform: WearOSMessagePlatform

    @Before
    fun setUp() {
        messagePlatform = WearOSMessagePlatform(messageClientTestRule.messageClient)
    }

    @Test
    fun `sendMessage passes high priority requests to MessageClient`() {
        val message = "message"
        val watchId = "watchId"

        // Send the message
        runBlocking {
            messagePlatform.sendMessage(watchId, message, priority = Message.Priority.HIGH)
        }

        // Verify the call was made
        verify {
            messageClientTestRule.messageClient.sendMessage(
                watchId,
                message,
                null,
                match { it.priority == MessageOptions.MESSAGE_PRIORITY_HIGH }
            )
        }
    }

    @Test
    fun `sendMessage passes low priority requests to MessageClient`() {
        val message = "message"
        val watchId = "watchId"

        // Send the message
        runBlocking {
            messagePlatform.sendMessage(watchId, message, priority = Message.Priority.LOW)
        }

        // Verify the call was made
        verify {
            messageClientTestRule.messageClient.sendMessage(
                watchId,
                message,
                null,
                match { it.priority == MessageOptions.MESSAGE_PRIORITY_LOW }
            )
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `incomingMessages gets all messages received by OnMessageReceivedListener`() {
        val messages = createMessagesFor(10, messagePlatform.platformIdentifier)

        val scope = CoroutineScope(Dispatchers.Default)

        // Start collecting messages
        val collectedMessages = mutableListOf<Message>()
        scope.launch {
            messagePlatform.incomingMessages().collect {
                collectedMessages += it
            }
        }

        // Send the dummy messages
        messages.forEach {
            messageClientTestRule.receiveMessage(
                it.first, it.second.message, it.second.data
            )
        }

        // Cancel message collection
        scope.cancel()

        // Make sure we got all the messages
        expectThat(collectedMessages).containsExactlyInAnyOrder(messages.map { it.second })
    }
}
