package com.boswelja.watchconnection.wearos.message

import android.content.Context
import com.boswelja.watchconnection.common.message.Message
import com.boswelja.watchconnection.common.message.MessagePriority
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.MessageClient as GMSMessageClient
import com.google.android.gms.wearable.MessageOptions
import com.google.android.gms.wearable.Wearable
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MessageClientTest {

    private lateinit var context: Context
    private lateinit var gmsMessageClient: GMSMessageClient
    private lateinit var messageClient: MessageClient

    @Before
    fun setUp() {
        context = mockk()
        every { context.applicationContext } returns context
        gmsMessageClient = mockk()

        mockkStatic(Wearable::class)
        every { Wearable.getMessageClient(any<Context>()) } returns gmsMessageClient

        messageClient = MessageClient(context, listOf())
    }

    @Test
    fun sendRawMessage() {
        // Set up data and mocks
        val targetId = "target"
        val message = Message("message", ByteArray(10))
        every {
            gmsMessageClient.sendMessage(any(), any(), any(), any())
        } returns Tasks.forResult(1)

        // Make the call
        runBlocking {
            messageClient.sendRawMessage(message, MessagePriority.HIGH)
        }

        // Verify the result
        verify {
            gmsMessageClient.sendMessage(
                targetId,
                message.path,
                message.data,
                MessageOptions(MessageOptions.MESSAGE_PRIORITY_HIGH)
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun rawIncomingMessages() {
        // Set up data and mocks
        val messageCount = 10
        var listener: GMSMessageClient.OnMessageReceivedListener? = null
        every { gmsMessageClient.addListener(any()) } answers {
            listener = firstArg()
            Tasks.forResult(null)
        }

        // Start collecting messages
        val scope = TestCoroutineScope()
        val collectedMessages = mutableListOf<ReceivedMessage<ByteArray?>>()
        scope.launch {
            messageClient.rawIncomingMessages().collect { collectedMessages.add(it) }
        }

        // Emulate receiving messages
        (0 until messageCount).forEach {
            listener?.onMessageReceived(DummyMessageEvent(it.toString(), it.toString()))
        }

        // Check the result
        Assert.assertEquals(messageCount, collectedMessages.count())
    }
}
