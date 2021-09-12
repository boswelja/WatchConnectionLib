package com.boswelja.watchconnection.wearos.discovery

import android.content.Context
import io.mockk.mockk
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private const val TIMEOUT = 250L

public class WearOSDiscoveryPlatformTest {

    private val appCapability = "app-capability"
    private val capabilities = (0..4).map { it.toString() }

    private lateinit var context: Context
    private lateinit var nodeClient: DummyNodeClient
    private lateinit var capabilityClient: DummyCapabilityClient
    private lateinit var discoveryPlatform: WearOSDiscoveryPlatform

    @BeforeEach
    public fun setUp() {
        context = mockk()
        nodeClient = DummyNodeClient(context)
        capabilityClient = DummyCapabilityClient(context)
        discoveryPlatform = WearOSDiscoveryPlatform(
            appCapability,
            capabilities,
            nodeClient,
            capabilityClient
        )
    }

    @Test
    public fun `allWatches flows initial value immediately`() {
        val nodes = createNodes(5)
        nodeClient.connectedNodes.addAll(nodes)

        val watches = runBlocking {
            withTimeout(TIMEOUT) {
                discoveryPlatform.allWatches().first()
            }
        }

        assertEquals(nodes.map { it.id }, watches.map { it.internalId })
    }

    @Test
    public fun `watchesWithApp flows initial value immediately`() {
        // Set the capable nodes
        val nodes = createNodes(5)
        capabilityClient.nodesWithCapability[appCapability] = nodes.toMutableSet()

        // Take the first emission, if any
        val watches = runBlocking {
            withTimeout(TIMEOUT) {
                discoveryPlatform.watchesWithApp().first()
            }
        }

        assertEquals(nodes.map { it.id }, watches.map { it.internalId })
    }

    @Test
    public fun `getCapabilitiesFor flows initial value immediately`() {
        // Set the capable nodes
        val nodes = createNodes(5)
        capabilities.forEach {
            capabilityClient.nodesWithCapability[it] = nodes.toMutableSet()
        }

        // Take the first emission, if any
        val capabilities = runBlocking {
            withTimeout(TIMEOUT) {
                discoveryPlatform.getCapabilitiesFor(nodes.first().id).first()
            }
        }

        // We've just mocked all capabilities, so check for them all
        assertEquals(this.capabilities, capabilities)
    }

    @Test
    public fun `getStatusFor flows initial value immediately`() {
        // Take the first emission, if any
        val status = runBlocking {
            withTimeoutOrNull(TIMEOUT) {
                discoveryPlatform.getStatusFor("watchid").first()
            }
        }

        // If status isn't null, we got something
        assertNotNull(status)
    }
}
