package com.boswelja.watchconnection.core.discovery

import com.boswelja.watchconnection.common.discovery.ConnectionMode
import com.boswelja.watchconnection.createCapabilities
import com.boswelja.watchconnection.createWatchesFor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class DiscoveryClientTest {

    private val testScope = CoroutineScope(EmptyCoroutineContext)
    private val allWatches = createWatchesFor(5, ConcreteDiscoveryPlatform.PLATFORM)
    private val capabilities = createCapabilities(4)
    private val connectionMode = ConnectionMode.Bluetooth
    private val platform = ConcreteDiscoveryPlatform(
        allWatches,
        capabilities,
        connectionMode
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
    fun discoveryClientFlowsCapabilitiesFromTheCorrectPlatform() {
        testScope.launch {
            val client = DiscoveryClient(listOf(platform))
            allWatches.forEach { watch ->
                assertEquals(capabilities, client.getCapabilitiesFor(watch))
            }
        }
    }
}
