package com.boswelja.watchconnection.core.discovery

import com.boswelja.watchconnection.common.discovery.Status
import com.boswelja.watchconnection.createCapabilities
import com.boswelja.watchconnection.createWatchesFor
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class DiscoveryClientTest {

    private val testScope = CoroutineScope(EmptyCoroutineContext)
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
    fun discoveryClientFlowsAllWatchesFromAllPlatforms() {
        testScope.launch {
            val client = DiscoveryClient(listOf(platform))
            client.allWatches().take(1).collect {
                assertEquals(allWatches, it)
            }
        }
    }

    @Test
    fun discoveryClientFlowsWatchesWithAppFromAllPlatforms() {
        testScope.launch {
            val client = DiscoveryClient(listOf(platform))
            client.watchesWithApp().take(1).collect {
                assertEquals(watchesWithApp, it)
            }
        }
    }

    @Test
    fun discoveryClientFlowsCapabilitiesFromTheCorrectPlatform() {
        testScope.launch {
            val client = DiscoveryClient(listOf(platform))
            allWatches.forEach { watch ->
                client.getCapabilitiesFor(watch)!!.take(1).collect {
                    assertEquals(capabilities, it)
                }
            }
        }
    }

    @Test
    fun discoveryClientFlowsStatusFromTheCorrectPlatform() {
        testScope.launch {
            val client = DiscoveryClient(listOf(platform))
            allWatches.forEach { watch ->
                client.getStatusFor(watch)!!.take(1).collect {
                    assertEquals(status, it)
                }
            }
        }
    }
}
