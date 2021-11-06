package com.boswelja.watchconnection.common.message

import android.content.Context
import android.content.Intent
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

public class MessageReceiverUtilsTest {

    private val packageName = "com.boswelja.watchconnection.common"

    private lateinit var context: Context

    @Before
    public fun setUp() {
        context = mockk()
        every { context.packageName } returns packageName
    }

    @Test
    public fun sendMessageReceivedBroadcast_failsWithEmptyUid() {
        assertFails {
            MessageReceiverUtils.sendMessageReceivedBroadcast(
                context,
                "",
                "path",
                null
            )
        }
    }

    @Test
    public fun sendMessageReceivedBroadcast_failsWithEmptyPath() {
        assertFails {
            MessageReceiverUtils.sendMessageReceivedBroadcast(
                context,
                "uid",
                "",
                null
            )
        }
    }

    @Test
    public fun sendMessageReceivedBroadcast_succeedsWithValidData() {
        // Create test data
        val uid = "uid"
        val path = "path"
        val data = byteArrayOf()

        // Set up mocks
        val intentData = mutableMapOf<String, Any?>()
        var setPackage: String? = null
        every { context.sendBroadcast(any()) } just Runs
        mockkConstructor(Intent::class)
        every { anyConstructed<Intent>().putExtra(any(), any<String>()) } answers {
            intentData[firstArg()] = secondArg<String>()
            mockk()
        }
        every { anyConstructed<Intent>().putExtra(any(), any<ByteArray>()) } answers {
            intentData[firstArg()] = secondArg<ByteArray>()
            mockk()
        }
        every { anyConstructed<Intent>().setPackage(any()) } answers {
            setPackage = firstArg()
            mockk()
        }

        // Make the call
        MessageReceiverUtils.sendMessageReceivedBroadcast(
            context,
            uid,
            path,
            data
        )

        // Verify results
        verify { context.sendBroadcast(any()) }
        assertEquals(uid, intentData[MessageReceiverUtils.SenderUid])
        assertEquals(path, intentData[MessageReceiverUtils.MessagePath])
        assertEquals(data, intentData[MessageReceiverUtils.MessageData])
        assertEquals(packageName, setPackage)
    }

    @Test
    public fun getReceivedMessageFromIntent_failsWithMissingSenderUid() {
        val intent = createIntentWith(
            "",
            "path",
            byteArrayOf()
        )
        assertFails {
            MessageReceiverUtils.getReceivedMessageFromIntent(intent)
        }
    }


    @Test
    public fun getReceivedMessageFromIntent_failsWithMissingMessagePath() {
        val intent = createIntentWith(
            "uid",
            "",
            byteArrayOf()
        )
        assertFails {
            MessageReceiverUtils.getReceivedMessageFromIntent(intent)
        }
    }


    @Test
    public fun getReceivedMessageFromIntent_succeedsWithValidData() {
        // Create data
        val uid = "uid"
        val path = "path"
        val data = byteArrayOf()

        // Make the call
        val intent = createIntentWith(
            uid,
            path,
            data
        )
        val receivedMessage = MessageReceiverUtils.getReceivedMessageFromIntent(intent)

        // Check results
        assertEquals(uid, receivedMessage.sourceUid)
        assertEquals(path, receivedMessage.path)
        assertEquals(data, receivedMessage.data)
    }

    private fun createIntentWith(
        senderUid: String,
        messagePath: String,
        messageData: ByteArray?
    ): Intent {
        return mockk {
            every { getStringExtra(MessageReceiverUtils.SenderUid) } returns senderUid
            every { getStringExtra(MessageReceiverUtils.MessagePath) } returns messagePath
            every { getByteArrayExtra(MessageReceiverUtils.MessageData) } returns messageData
        }
    }
}
