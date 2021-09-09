package com.boswelja.watchconnection.core.message

import com.boswelja.watchconnection.common.message.ByteArrayMessage
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.boswelja.watchconnection.common.message.serialized.TypedMessage
import com.boswelja.watchconnection.core.Watch
import com.boswelja.watchconnection.core.message.serialized.ConcreteDataType
import com.boswelja.watchconnection.core.message.serialized.ConcreteMessageSerializer
import com.boswelja.watchconnection.core.message.serialized.MessagePath
import com.boswelja.watchconnection.createWatchesFor
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MessageClientTest {

    private val scope = CoroutineScope(Dispatchers.Main.immediate)
    private val serializers = listOf(ConcreteMessageSerializer)

    private lateinit var dummyWatches: Map<String, List<Watch>>
    private lateinit var platforms: List<ConcreteMessagePlatform>
    private lateinit var client: MessageClient

    @BeforeTest
    fun setUp() {
        platforms = createPlatforms(5)
        client = MessageClient(
            serializers = serializers,
            platforms = platforms
        )
        dummyWatches = platforms.associate { platform ->
            val watches = createWatchesFor(5, platform.platformIdentifier)
            Pair(platform.platformIdentifier, watches)
        }
    }

    @Test
    fun sendMessageSerializesMessageIfSerializerRegistered() {
        scope.launch {
            // Create a dummy message
            val message = TypedMessage(
                MessagePath,
                ConcreteDataType("data")
            )
            val expectedBytes = message.data.data.encodeToByteArray()

            // Test against all platforms and all watches to be safe
            platforms.forEach { platform ->
                val watches = dummyWatches[platform.platformIdentifier]!!

                watches.forEach { watch ->
                    client.sendMessage(watch, message)
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
    fun sendMessageThrowsExceptionIfSerializableMessageHasNullData() {
        // Create a dummy message with wrong data type
        val message = TypedMessage(
            MessagePath,
            null
        )

        // Test against all platforms and all watches to be safe
        platforms.forEach { platform ->
            val watches = dummyWatches[platform.platformIdentifier]!!

            watches.forEach { watch ->
                assertFailsWith<IllegalArgumentException> {
                    scope.launch { client.sendMessage(watch, message) }
                }
            }

            assertEquals(0, platform.sentMessages.count())
        }
    }

    @Test
    fun sendMessageSendsNonSerializableByteArrayMessageCorrectly() {
        scope.launch {
            // Create a dummy message
            val expectedBytes = "data".encodeToByteArray()
            val message = ByteArrayMessage(
                "nonserialized-path",
                expectedBytes
            )

            // Test against all platforms and all watches to be safe
            platforms.forEach { platform ->
                val watches = dummyWatches[platform.platformIdentifier]!!

                watches.forEach { watch ->
                    client.sendMessage(watch, message)
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
    fun sendMessageSendsNonSerializableByteArrayMessageWithNullDataCorrectly() {
        scope.launch {
            // Create a dummy message
            val message = ByteArrayMessage(
                "nonserialized-path",
                null
            )

            // Test against all platforms and all watches to be safe
            platforms.forEach { platform ->
                val watches = dummyWatches[platform.platformIdentifier]!!

                watches.forEach { watch ->
                    client.sendMessage(watch, message)
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun rawIncomingMessagesFlowsMessagesFromPlatforms() {
        // Start collecting incoming messages
        val receivedMessages = mutableListOf<ReceivedMessage<ByteArray?>>()
        scope.launch {
            client.rawIncomingMessages().collect { message ->
                receivedMessages.add(message)
            }
        }

        // Mock sending messages
        val expectedMessages = mutableListOf<ReceivedMessage<ByteArray?>>()
        scope.launch {
            platforms.forEach { platform ->
                val watches = dummyWatches[platform.platformIdentifier]!!
                watches.forEach { watch ->
                    val message = ReceivedMessage<ByteArray?>(
                        watch.uid,
                        "message",
                        ByteArray(10) { 1 }
                    )
                    platform.incomingMessages.emit(message)
                    expectedMessages.add(message)
                }
            }
        }

        // Check all messages were received
        assertEquals(expectedMessages, receivedMessages)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun incomingMessagesDeserializesMessagesWhenPossible() {
        scope.launch {
            val expectedData = ConcreteDataType("Data")
            val dataBytes = ConcreteMessageSerializer.serialize(expectedData)

            // Start collecting incoming messages
            val receivedMessages = mutableListOf<ReceivedMessage<*>>()
            scope.launch {
                client.incomingMessages().collect { message ->
                    receivedMessages.add(message)
                }
            }

            // Mock sending messages
            val expectedMessages = mutableListOf<ReceivedMessage<*>>()
            platforms.forEach { platform ->
                val watches = dummyWatches[platform.platformIdentifier]!!
                watches.forEach { watch ->
                    val message = ReceivedMessage<ByteArray?>(
                        watch.uid,
                        MessagePath,
                        dataBytes
                    )
                    platform.incomingMessages.emit(message)
                    expectedMessages.add(message)
                }
            }

            // Check all messages were received
            assertEquals(expectedMessages.count(), receivedMessages.count())
            receivedMessages.forEach {
                assertEquals(expectedData, it.data)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun incomingMessagesThrowsExceptionWhenSerializableMessageDataIsNull() {
        // Start collecting incoming messages
        val receivedMessages = mutableListOf<ReceivedMessage<*>>()
        scope.launch {
            client.incomingMessages()
                .catch { cause ->
                    assertTrue(cause is ClassCastException)
                }.collect { message ->
                    receivedMessages.add(message)
                }
        }

        // Mock sending messages
        scope.launch {
            platforms.forEach { platform ->
                val watches = dummyWatches[platform.platformIdentifier]!!
                watches.forEach { watch ->
                    val message = ReceivedMessage<ByteArray?>(
                        watch.uid,
                        MessagePath,
                        null
                    )
                    platform.incomingMessages.emit(message)
                }
            }
        }

        // Check all messages were received
        assertEquals(0, receivedMessages.count())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun incomingMessagesWithSerializerThrowsExceptionWhenSerializableMessageDataIsNull() {
        // Start collecting incoming messages
        val receivedMessages = mutableListOf<ReceivedMessage<ConcreteDataType>>()
        scope.launch {
            client.incomingMessages(ConcreteMessageSerializer)
                .catch { cause ->
                    assertTrue(cause is ClassCastException)
                }.collect { message ->
                    receivedMessages.add(message)
                }
        }

        // Mock sending messages
        scope.launch {
            platforms.forEach { platform ->
                val watches = dummyWatches[platform.platformIdentifier]!!
                watches.forEach { watch ->
                    val message = ReceivedMessage<ByteArray?>(
                        watch.uid,
                        MessagePath,
                        null
                    )
                    platform.incomingMessages.emit(message)
                }
            }
        }

        // Check all messages were received
        assertEquals(0, receivedMessages.count())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun incomingMessagesWithSerializerOnlyEmitsSupportedMessages() {
        val data = ConcreteDataType("data")

        // Start collecting incoming messages
        val receivedMessages = mutableListOf<ReceivedMessage<ConcreteDataType>>()
        scope.launch {
            client.incomingMessages(ConcreteMessageSerializer)
                .collect { message ->
                    receivedMessages.add(message)
                }
        }

        // Mock sending messages
        val expectedMessages = mutableListOf<ReceivedMessage<ByteArray?>>()
        scope.launch {
            platforms.forEach { platform ->
                val watches = dummyWatches[platform.platformIdentifier]!!
                watches.forEachIndexed { index, watch ->
                    if (index % 2 == 0) {
                        val message = ReceivedMessage<ByteArray?>(
                            watch.uid,
                            MessagePath,
                            ConcreteMessageSerializer.serialize(data)
                        )
                        platform.incomingMessages.emit(message)
                        expectedMessages.add(message)
                    } else {
                        val message = ReceivedMessage<ByteArray?>(
                            watch.uid,
                            "invalid-path",
                            null
                        )
                        platform.incomingMessages.emit(message)
                    }
                }
            }
        }

        // Check all messages were received
        receivedMessages.forEach {
            assertEquals(MessagePath, it.path)
            assertEquals(data, it.data)
        }
    }
}
