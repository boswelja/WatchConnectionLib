package com.boswelja.watchconnection.core.message

import com.boswelja.watchconnection.core.Watch
import com.boswelja.watchconnection.core.message.serialized.ConcreteDataSerializer
import com.boswelja.watchconnection.core.message.serialized.ConcreteDataType
import com.boswelja.watchconnection.core.message.serialized.MessagePath
import com.boswelja.watchconnection.core.message.serialized.TypedMessage
import com.boswelja.watchconnection.createWatchesFor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Before
import org.junit.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.all
import strikt.assertions.containsExactlyInAnyOrder
import strikt.assertions.count
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo
import strikt.assertions.isNull

class MessageClientTest {

    private val serializers = listOf(ConcreteDataSerializer)

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
            expectThat(platform.sentMessages) {
                count().isEqualTo(watches.count())
                all { get { data }.isEqualTo(expectedBytes) }
            }
        }
    }

    @Test
    fun `sendMessage throws exception if serializable message has null data`(): Unit = runBlocking {
        // Create a dummy message with wrong data type
        val message = TypedMessage(
            MessagePath,
            null
        )

        // Test against all platforms and all watches to be safe
        platforms.forEach { platform ->
            val watches = dummyWatches[platform.platformIdentifier]!!

            watches.forEach { watch ->
                expectThrows<IllegalArgumentException> { client.sendMessage(watch, message) }
            }

            expectThat(platform.sentMessages).isEmpty()
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
            expectThat(platform.sentMessages) {
                count().isEqualTo(watches.count())
                all { get { data }.isEqualTo(expectedBytes) }
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
                expectThat(platform.sentMessages) {
                    count().isEqualTo(watches.count())
                    all { get { data }.isNull() }
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
                        watch.id,
                        "message",
                        null
                    )
                    platform.incomingMessages.emit(message)
                    expectedMessages.add(message)
                }
            }
        }

        // Check all messages were received
        expectThat(receivedMessages).containsExactlyInAnyOrder(expectedMessages)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `incomingMessages deserializes messages when possible`() {
        val expectedData = ConcreteDataType("Data")
        val dataBytes = runBlocking { ConcreteDataSerializer.serialize(expectedData) }

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
                        watch.id,
                        MessagePath,
                        dataBytes
                    )
                    platform.incomingMessages.emit(message)
                    expectedMessages.add(message)
                }
            }
        }

        // Check all messages were received
        expectThat(receivedMessages) {
            count().isEqualTo(expectedMessages.count())
            all { get { data }.isEqualTo(expectedData) }
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
                    expectThat(cause is ClassCastException)
                }.collect { message ->
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
                        watch.id,
                        MessagePath,
                        null
                    )
                    platform.incomingMessages.emit(message)
                    expectedMessages.add(message)
                }
            }
        }

        // Check all messages were received
        expectThat(receivedMessages).isEmpty()
    }
}
