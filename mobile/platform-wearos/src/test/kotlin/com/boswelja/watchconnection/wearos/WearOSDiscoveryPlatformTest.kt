package com.boswelja.watchconnection.wearos

import com.boswelja.watchconnection.wearos.rules.CapabilityClientTestRule
import com.boswelja.watchconnection.wearos.rules.NodeClientTestRule
import com.boswelja.watchconnection.wearos.rules.createNodes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.isNotNull

private const val TIMEOUT = 250L

class WearOSDiscoveryPlatformTest {

    private val appCapability = "app-capability"
    private val capabilities = (0..4).map { it.toString() }

    @get:Rule
    val capabilityClientTestRule = CapabilityClientTestRule()
    @get:Rule
    val nodeClientTestRule = NodeClientTestRule()

    private lateinit var discoveryPlatform: WearOSDiscoveryPlatform

    @Before
    fun setUp() {
        discoveryPlatform = WearOSDiscoveryPlatform(
            appCapability,
            capabilities,
            nodeClientTestRule.nodeClient,
            capabilityClientTestRule.capabilityClient
        )
    }

    @Test
    fun `allWatches flows initial value immediately`() {
        val nodes = createNodes(5)
        nodeClientTestRule.setConnectedNodes(nodes)

        val watches = runBlocking {
            withTimeout(TIMEOUT) {
                discoveryPlatform.allWatches().first()
            }
        }

        expectThat(watches.map { it.platformId }).containsExactly(nodes.map { it.id })
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `watchesWithApp flows initial value immediately`() {
        // Set the capable nodes
        val nodes = createNodes(5)
        capabilityClientTestRule.changeCapabilityInfo(appCapability, nodes)

        // Take the first emission, if any
        val watches = runBlocking {
            withTimeout(TIMEOUT) {
                discoveryPlatform.watchesWithApp().first()
            }
        }

        expectThat(watches.map { it.platformId }).containsExactly(nodes.map { it.id })
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `getCapabilitiesFor flows initial value immediately`() {
        // Set the capable nodes
        val nodes = createNodes(5)
        capabilities.forEach {
            capabilityClientTestRule.changeCapabilityInfo(it, nodes)
        }

        // Take the first emission, if any
        val capabilities = runBlocking {
            withTimeout(TIMEOUT) {
                discoveryPlatform.getCapabilitiesFor(nodes.first().id).first()
            }
        }

        // We've just mocked all capabilities, so check for them all
        expectThat(capabilities).containsExactly(capabilities)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `getStatusFor flows initial value immediately`() {
        // Take the first emission, if any
        val status = runBlocking {
            withTimeoutOrNull(TIMEOUT) {
                discoveryPlatform.getStatusFor("watchid").first()
            }
        }

        // If status isn't null, we got something
        expectThat(status).isNotNull()
    }
}
