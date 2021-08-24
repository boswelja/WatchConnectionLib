package com.boswelja.watchconnection.wearos.rules

import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.MessageClient
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MessageClientTestRule : TestRule {

    private var messageListener: MessageClient.OnMessageReceivedListener? = null

    lateinit var messageClient: MessageClient

    override fun apply(base: Statement?, description: Description?): Statement {
        return MessageClientTestRuleStatement(base)
    }

    fun receiveMessage(sourceNodeId: String, message: String, data: ByteArray? = null) {
        val messageListener = runBlocking {
            withTimeoutOrNull(1000L) {
                withContext(Dispatchers.Default) {
                    while (messageListener == null) {
                        continue
                    }
                }

                messageListener
            }
        } ?: throw NullPointerException("No message listener has been added")

        messageListener.onMessageReceived(DummyMessageEvent(sourceNodeId, message, data))
    }

    inner class MessageClientTestRuleStatement(private val base: Statement?) : Statement() {
        override fun evaluate() {
            // Set up MessageClient mock
            messageClient = mockk(relaxed = true)
            every { messageClient.sendMessage(any(), any(), any()) } returns Tasks.forResult(0)
            every {
                messageClient.sendMessage(any(), any(), any(), any())
            } returns Tasks.forResult(0)
            every { messageClient.addListener(any()) } answers {
                messageListener = firstArg() as MessageClient.OnMessageReceivedListener
                Tasks.forResult(null)
            }

            try {
                base?.evaluate()
            } finally { }
        }
    }
}
