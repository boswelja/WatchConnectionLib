package com.boswelja.watchconnection.core.discovery

import com.boswelja.watchconnection.common.discovery.Status
import com.boswelja.watchconnection.createCapabilities
import com.boswelja.watchconnection.createWatchesFor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

@ExperimentalCoroutinesApi
class DiscoveryClientTest {

    private val allWatches = createWatchesFor(5, ConcreteDiscoveryPlatform.PLATFORM)
    private val watchesWithApp = createWatchesFor(3, ConcreteDiscoveryPlatform.PLATFORM)
    private val capabilities = createCapabilities(4)
    private val status = Status.CONNECTED
    private val platform = ConcreteDiscoveryPlatform(
        allWatches,
        watchesWithApp,
        capabilities,
        status
    )

    @Test
    fun `DiscoveryClient flows allWatches from all platforms`(): Unit = runBlocking {
        val client = DiscoveryClient(listOf(platform))
        client.allWatches().take(1).collect {
            Assert.assertEquals(allWatches, it)
        }
    }

    @Test
    fun `DiscoveryClient flows watchesWithApp from all platforms`(): Unit = runBlocking {
        val client = DiscoveryClient(listOf(platform))
        client.watchesWithApp().take(1).collect {
            Assert.assertEquals(watchesWithApp, it)
        }
    }

    @Test
    fun `DiscoveryClient flows capabilities from the correct platform`(): Unit = runBlocking {
        val client = DiscoveryClient(listOf(platform))
        allWatches.forEach { watch ->
            client.getCapabilitiesFor(watch)!!.take(1).collect {
                Assert.assertEquals(capabilities, it)
            }
        }
    }

    @Test
    fun `DiscoveryClient flows status from the correct platform`(): Unit = runBlocking {
        val client = DiscoveryClient(listOf(platform))
        allWatches.forEach { watch ->
            client.getStatusFor(watch)!!.take(1).collect {
                Assert.assertEquals(status, it)
            }
        }
    }
}
