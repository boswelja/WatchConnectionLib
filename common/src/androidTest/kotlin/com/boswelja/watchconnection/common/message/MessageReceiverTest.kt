package com.boswelja.watchconnection.common.message

import android.content.Intent
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.yield
import org.junit.Test
import kotlin.test.assertEquals

public class MessageReceiverTest {

    @Test
    public fun onReceive_ignoresIntentWithIncorrectAction() {
        val intent = createIntentWith(
            "some-action",
            "uid",
            "path",
            byteArrayOf()
        )

        mockkObject(MessageReceiverUtils)
        every { MessageReceiverUtils.getReceivedMessageFromIntent(any()) } throws AssertionError()

        ConcreteMessageReceiver().onReceive(mockk(), intent)
    }

    @Test
    public fun onReceive_passesValidMessageToImpl(): Unit = runBlocking {
        // Create data
        val uid = "uid"
        val path = "path"
        val data = byteArrayOf()
        val intent = createIntentWith(
            MessageReceiverUtils.MessageReceived,
            uid,
            path,
            data
        )

        // Set up mocks
        val receiver = spyk(ConcreteMessageReceiver())
        mockkObject(MessageReceiverUtils)
        every { MessageReceiverUtils.getReceivedMessageFromIntent(any()) } returns
            ReceivedMessage(uid, path, data)
        every { receiver.goAsync() } returns mockk {
            every { finish() } just Runs
        }

        // Make the call
        receiver.onReceive(mockk(), intent)

        // Check the result
        val message = withTimeout(2000L) {
            while (receiver.receivedMessage == null) {
                yield()
                continue
            }
            receiver.receivedMessage!!
        }
        assertEquals(uid, message.sourceUid)
        assertEquals(path, message.path)
        assertEquals(data, message.data)
    }

    @Test
    public fun onReceive_handlesExceptions(): Unit = runBlocking {
        // Create data
        val uid = "uid"
        val path = "path"
        val data = byteArrayOf()
        val intent = createIntentWith(
            MessageReceiverUtils.MessageReceived,
            uid,
            path,
            data
        )

        // Set up mocks
        val receiver = spyk(ConcreteMessageReceiver())
        var hasFinished = false
        mockkObject(MessageReceiverUtils)
        every { MessageReceiverUtils.getReceivedMessageFromIntent(any()) } returns
            ReceivedMessage(uid, path, data)
        every { receiver.goAsync() } returns mockk {
            every { finish() } answers {
                hasFinished = true
            }
        }
        // Throw an exception for onMessageReceived
        coEvery { receiver.onMessageReceived(any(), any()) } throws Exception()

        // Make the call
        receiver.onReceive(mockk(), intent)

        withTimeout(2000L) {
            while (!hasFinished) {
                yield()
                continue
            }
        }
    }

    private fun createIntentWith(
        action: String,
        senderUid: String,
        messagePath: String,
        messageData: ByteArray?
    ): Intent {
        return mockk {
            every { getAction() } returns action
            every { getStringExtra(MessageReceiverUtils.SenderUid) } returns senderUid
            every { getStringExtra(MessageReceiverUtils.MessagePath) } returns messagePath
            every { getByteArrayExtra(MessageReceiverUtils.MessageData) } returns messageData
        }
    }
}
