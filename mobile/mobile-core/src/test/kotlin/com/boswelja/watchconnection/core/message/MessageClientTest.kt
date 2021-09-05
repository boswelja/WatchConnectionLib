package com.boswelja.watchconnection.core.message

import com.boswelja.watchconnection.common.message.ByteArrayMessage
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.boswelja.watchconnection.common.message.serialized.TypedMessage
import com.boswelja.watchconnection.core.Watch
import com.boswelja.watchconnection.core.message.serialized.ConcreteDataType
import com.boswelja.watchconnection.core.message.serialized.ConcreteMessageSerializer
import com.boswelja.watchconnection.core.message.serialized.MessagePath
import com.boswelja.watchconnection.createWatchesFor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MessageClientTest {

    private val serializers = listOf(ConcreteMessageSerializer)

    private lateinit var dummyWatches: Map<String, List<Watch>>
    private lateinit var platforms: List<ConcreteMessagePlatform>
    private lateinit var client: MessageClient

    @Before
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
    fun `sendMessage serializes message if serializer registered`(): Unit = runBlocking {
        // Create a dummy message
        val message = TypedMessage(
            MessagePath,
            ConcreteDataType("data")
        )
        val expectedBytes = message.data.data.toByteArray()

        // Test against all platforms and all watches to be safe
        platforms.forEach { platform ->
            val watches = dummyWatches[platform.platformIdentifier]!!

            watches.forEach { watch ->
                client.sendMessage(watch, message)
            }

            // Check received messages
            Assert.assertEquals(watches.count(), platform.sentMessages.count())
            platform.sentMessages.forEach {
                Assert.assertArrayEquals(expectedBytes, it.data)
            }
        }
    }

    @Test
    fun `sendMessage throws exception if serializable message has null data`() {
        // Create a dummy message with wrong data type
        val message = TypedMessage(
            MessagePath,
            null
        )

        // Test against all platforms and all watches to be safe
        platforms.forEach { platform ->
            val watches = dummyWatches[platform.platformIdentifier]!!

            watches.forEach { watch ->
                Assert.assertThrows(IllegalArgumentException::class.java) {
                    runBlocking { client.sendMessage(watch, message) }
                }
            }

            Assert.assertEquals(0, platform.sentMessages.count())
        }
    }

    @Test
    fun `sendMessage sends non-serializable ByteArray message correctly`(): Unit = runBlocking {
        // Create a dummy message
        val expectedBytes = "data".toByteArray()
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
            Assert.assertEquals(watches.count(), platform.sentMessages.count())
            platform.sentMessages.forEach {
                Assert.assertArrayEquals(expectedBytes, it.data)
            }
        }
    }

    @Test
    fun `sendMessage sends non-serializable ByteArray message with null data correctly`(): Unit =
        runBlocking {
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
                Assert.assertEquals(watches.count(), platform.sentMessages.count())
                platform.sentMessages.forEach {
                    Assert.assertNull(it.data)
                }
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `incomingMessages flows messages from platforms`() {
        // Start collecting incoming messages
        val receivedMessages = mutableListOf<ReceivedMessage<*>>()
        val scope = TestCoroutineScope()
        scope.launch {
            client.incomingMessages().collect { message ->
                receivedMessages.add(message)
            }
        }

        // Mock sending messages
        val expectedMessages = mutableListOf<ReceivedMessage<*>>()
        runBlocking {
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
        Assert.assertEquals(expectedMessages, receivedMessages)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `rawIncomingMessages flows messages from platforms`() {
        // Start collecting incoming messages
        val receivedMessages = mutableListOf<ReceivedMessage<ByteArray?>>()
        val scope = TestCoroutineScope()
        scope.launch {
            client.rawIncomingMessages().collect { message ->
                receivedMessages.add(message)
            }
        }

        // Mock sending messages
        val expectedMessages = mutableListOf<ReceivedMessage<ByteArray?>>()
        runBlocking {
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
        Assert.assertEquals(expectedMessages, receivedMessages)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `incomingMessages deserializes messages when possible`() {
        val expectedData = ConcreteDataType("Data")
        val dataBytes = runBlocking { ConcreteMessageSerializer.serialize(expectedData) }

        // Start collecting incoming messages
        val receivedMessages = mutableListOf<ReceivedMessage<*>>()
        val scope = TestCoroutineScope()
        scope.launch {
            client.incomingMessages().collect { message ->
                receivedMessages.add(message)
            }
        }

        // Mock sending messages
        val expectedMessages = mutableListOf<ReceivedMessage<*>>()
        runBlocking {
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
        }

        // Check all messages were received
        Assert.assertEquals(expectedMessages.count(), receivedMessages.count())
        receivedMessages.forEach {
            Assert.assertEquals(expectedData, it.data)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `incomingMessages throws exception when serializable message data is null`() {
        // Start collecting incoming messages
        val receivedMessages = mutableListOf<ReceivedMessage<*>>()
        val scope = TestCoroutineScope()
        scope.launch {
            client.incomingMessages()
                .catch { cause ->
                    Assert.assertTrue(cause is ClassCastException)
                }.collect { message ->
                    receivedMessages.add(message)
                }
        }

        // Mock sending messages
        runBlocking {
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
        Assert.assertEquals(0, receivedMessages.count())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `incomingMessages(serializer) throws exception when serializable message data is null`() {
        // Start collecting incoming messages
        val receivedMessages = mutableListOf<ReceivedMessage<ConcreteDataType>>()
        val scope = TestCoroutineScope()
        scope.launch {
            client.incomingMessages(ConcreteMessageSerializer)
                .catch { cause ->
                    Assert.assertTrue(cause is ClassCastException)
                }.collect { message ->
                    receivedMessages.add(message)
                }
        }

        // Mock sending messages
        runBlocking {
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
        Assert.assertEquals(0, receivedMessages.count())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `incomingMessages(serializer) only emits supported messages`() {
        val data = ConcreteDataType("data")

        // Start collecting incoming messages
        val receivedMessages = mutableListOf<ReceivedMessage<ConcreteDataType>>()
        val scope = TestCoroutineScope()
        scope.launch {
            client.incomingMessages(ConcreteMessageSerializer)
                .collect { message ->
                    receivedMessages.add(message)
                }
        }

        // Mock sending messages
        val expectedMessages = mutableListOf<ReceivedMessage<ByteArray?>>()
        runBlocking {
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
            Assert.assertEquals(MessagePath, it.path)
            Assert.assertEquals(data, it.data)
        }
    }
}
