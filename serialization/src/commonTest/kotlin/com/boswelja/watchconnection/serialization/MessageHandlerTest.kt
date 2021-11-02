package com.boswelja.watchconnection.serialization

import app.cash.turbine.test
import com.boswelja.watchconnection.common.message.Message
import com.boswelja.watchconnection.common.message.ReceivedMessage
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

class MessageHandlerTest {

    private val testSerializer = object : MessageSerializer<Boolean?> {
        override val messagePaths: Set<String> = setOf(
            "path1",
            "path2"
        )

        override suspend fun deserialize(bytes: ByteArray?): Boolean? {
            return bytes?.let { it.first().toInt() == 1 }
        }

        override suspend fun serialize(data: Boolean?): ByteArray {
            return data?.let {
                byteArrayOf(if (it) 1 else 0)
            } ?: byteArrayOf()
        }
    }

    private lateinit var messageClient: DummyMessageClient
    private lateinit var messageHandler: MessageHandler<Boolean?>

    @BeforeTest
    fun setUp() {
        messageClient = DummyMessageClient()
        messageHandler = MessageHandler(testSerializer, messageClient)
    }

    @Test
    fun incomingMessages_flowsNoUnsupportedMessages() = runBlockingTest {
        val testPaths = listOf(
            "path1Invalid",
            "invalidPath2"
        )

        messageHandler.incomingMessages().test {
            // Check test paths
            testPaths.forEach {
                messageClient.incomingMessages.emit(
                    ReceivedMessage("uid", it, null)
                )
                assertFails { awaitItem() }
            }
        }
    }

    @Test
    fun incomingMessages_flowsSupportedMessages() = runBlockingTest {
        val testPaths = testSerializer.messagePaths

        messageHandler.incomingMessages().test {
            // Check test paths
            testPaths.forEach {
                messageClient.incomingMessages.emit(
                    ReceivedMessage("uid", it, null)
                )
                assertEquals(it, awaitItem().path)
            }
        }
    }

    @Test
    fun incomingMessages_deserializesData() = runBlockingTest {
        val expectedValue = true
        val testData = testSerializer.serialize(expectedValue)
        val testMessage = ReceivedMessage<ByteArray?>(
            "uid",
            testSerializer.messagePaths.first(),
            testData
        )

        messageHandler.incomingMessages().test {
            // Check test paths
            messageClient.incomingMessages.emit(testMessage)
            assertEquals(expectedValue, awaitItem().data)
        }
    }

    @Test
    fun sendMessage_returnsClientResult() = runBlockingTest {
        val testMessage = Message<Boolean?>(
            testSerializer.messagePaths.first(),
            null
        )
        // Set client result to true and check
        messageClient.messageSendResult = true
        assertEquals(
            true,
            messageHandler.sendMessage("uid", testMessage)
        )
        // Set client result to false and check
        messageClient.messageSendResult = false
        assertEquals(
            false,
            messageHandler.sendMessage("uid", testMessage)
        )
    }

    @Test
    fun sendMessage_serializesData() = runBlockingTest {
        val testData = listOf(true, false)
        testData.forEach {
            val testMessage = Message<Boolean?>(
                testSerializer.messagePaths.first(),
                it
            )
            val expectedBytes = testSerializer.serialize(it)
            messageHandler.sendMessage("uid", testMessage)

            assertTrue { messageClient.sentMessage!!.data!!.contentEquals(expectedBytes) }
        }
    }
}
