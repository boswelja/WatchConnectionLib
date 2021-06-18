package com.boswelja.watchconnection.core.message

import com.boswelja.watchconnection.core.discovery.ConcreteDiscoveryPlatform
import com.boswelja.watchconnection.createWatchesFor
import java.util.UUID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isTrue

@ExperimentalCoroutinesApi
class MessageClientTest {

    private val watches = createWatchesFor(5, ConcreteDiscoveryPlatform.PLATFORM)
    private val message = Message(UUID.randomUUID(), "", null)
    private val platform = ConcreteMessagePlatform(message)

    @Test
    fun `incomingMessages collects from all platforms`(): Unit = runBlocking {
        val client = MessageClient(platform)
        client.incomingMessages().take(1).collect {
            expectThat(it).isEqualTo(message)
        }
    }

    @Test
    fun `sendMessage passes request to the correct platform`(): Unit = runBlocking {
        val client = MessageClient(platform)
        watches.forEach {
            // Result will be true if it made it to our ConcreteMessagePlatform
            val result = client.sendMessage(it, "message")
            expectThat(result).isTrue()
        }
    }
}
