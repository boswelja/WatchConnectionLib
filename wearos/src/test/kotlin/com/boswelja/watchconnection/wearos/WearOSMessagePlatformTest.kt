package com.boswelja.watchconnection.wearos

import com.boswelja.watchconnection.core.message.MessagePriority
import com.boswelja.watchconnection.core.message.ReceivedMessage
import com.boswelja.watchconnection.createMessagesFor
import com.boswelja.watchconnection.wearos.rules.MessageClientTestRule
import com.google.android.gms.wearable.MessageOptions
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
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
            messagePlatform.sendMessage(watchId, message, priority = MessagePriority.HIGH)
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
            messagePlatform.sendMessage(watchId, message, priority = MessagePriority.LOW)
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
        val messageCount = 10
        val messages = createMessagesFor(messageCount, messagePlatform.platformIdentifier)

        val scope = CoroutineScope(Dispatchers.Default)

        // Start collecting messages
        val job = Job()
        val collectedMessages = mutableListOf<ReceivedMessage<ByteArray?>>()
        scope.launch(job) {
            messagePlatform.incomingMessages().take(messageCount).collect {
                collectedMessages.add(it)
                println("Got $it")
            }
            println("Finished collection")
        }

        // Send the dummy messages
        messages.forEach {
            messageClientTestRule.receiveMessage(
                it.first, it.second.path, it.second.data
            )
        }

        // Wait for collection to finish
        job.complete()

        // Make sure we got all the messages
        expectThat(collectedMessages).containsExactlyInAnyOrder(messages.map { it.second })
    }
}
