package com.boswelja.watchconnection.core.message

import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.common.message.Message
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.boswelja.watchconnection.createWatchesFor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MessageClientTest {

    private lateinit var scope: CoroutineScope
    private lateinit var dummyWatches: Map<String, List<Watch>>
    private lateinit var platforms: List<ConcreteMessagePlatform>
    private lateinit var client: MessageClient

    @BeforeTest
    fun setUp() {
        scope = CoroutineScope(EmptyCoroutineContext)
        platforms = createPlatforms(5)
        client = MessageClient(
            platforms = platforms
        )
        dummyWatches = platforms.associate { platform ->
            val watches = createWatchesFor(5, platform.platformIdentifier)
            Pair(platform.platformIdentifier, watches)
        }
    }

    @Test
    fun sendMessageSendsMessageCorrectly() {
        scope.launch {
            // Create a dummy message
            val expectedBytes = "data".encodeToByteArray()
            val message = Message(
                "path",
                expectedBytes
            )

            // Test against all platforms and all watches to be safe
            platforms.forEach { platform ->
                val watches = dummyWatches[platform.platformIdentifier]!!

                watches.forEach { watch ->
                    client.sendMessage(watch.uid, message)
                }

                // Check received messages
                assertEquals(watches.count(), platform.sentMessages.count())
                platform.sentMessages.forEach {
                    assertEquals(expectedBytes, it.data)
                }
            }
        }
    }

    @Test
    fun sendMessageSendsMessageWithNullDataCorrectly() {
        scope.launch {
            // Create a dummy message
            val message = Message(
                "path",
                null
            )

            // Test against all platforms and all watches to be safe
            platforms.forEach { platform ->
                val watches = dummyWatches[platform.platformIdentifier]!!

                watches.forEach { watch ->
                    client.sendMessage(watch.uid, message)
                }

                // Check received messages
                assertEquals(watches.count(), platform.sentMessages.count())
                platform.sentMessages.forEach {
                    assertNull(it.data)
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun incomingMessagesFlowsMessagesFromPlatforms() {
        // Start collecting incoming messages
        val receivedMessages = mutableListOf<ReceivedMessage<*>>()
        scope.launch {
            client.incomingMessages().collect { message ->
                receivedMessages.add(message)
            }
        }

        // Mock sending messages
        val expectedMessages = mutableListOf<ReceivedMessage<*>>()
        scope.launch {
            platforms.forEach { platform ->
                val watches = dummyWatches[platform.platformIdentifier]!!
                watches.forEach { watch ->
                    val message = ReceivedMessage<ByteArray?>(
                        watch.uid,
                        "message",
                        null
                    )
                    platform.incomingMessages.emit(message)
                    expectedMessages.add(message)
                }
            }
        }

        // Check all messages were received
        assertEquals(expectedMessages, receivedMessages)
    }
}
