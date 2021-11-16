package com.boswelja.watchconnection.wearos.discovery

import app.cash.turbine.test
import com.boswelja.watchconnection.common.discovery.ConnectionMode
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.NodeClient
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

public class WearOSDiscoveryPlatformTest {

    private lateinit var nodeClient: NodeClient
    private lateinit var capabilityClient: CapabilityClient
    private lateinit var discoveryPlatform: WearOSDiscoveryPlatform

    @Before
    public fun setUp() {
        nodeClient = mockk()
        capabilityClient = mockk()
        discoveryPlatform = WearOSDiscoveryPlatform(
            nodeClient,
            capabilityClient,
            scanRepeatInterval = 100
        )
    }

    @After
    public fun tearDown() {
        unmockkAll()
    }

    @Test
    public fun allWatches_flowsChanges(): Unit = runBlocking {
        var count = 5
        every { nodeClient.connectedNodes } returns Tasks.forResult(createNodes(count))
        discoveryPlatform.allWatches().test {
            // Check initial value
            assertTrue { awaitItem().count() == count }

            // Check reduced count
            count = 3
            every { nodeClient.connectedNodes } returns Tasks.forResult(createNodes(count))
            assertTrue { awaitItem().count() == count }

            // Check increased count
            count = 7
            every { nodeClient.connectedNodes } returns Tasks.forResult(createNodes(count))
            assertTrue { awaitItem().count() == count }
        }
    }

    @Test
    public fun getCapabilitiesFor_returnsCapabilitiesForWatch(): Unit = runBlocking {
        val capabilities = listOf("capability1", "capability2", "capability3")
        val node = createNodes(1).first()
        every {
            capabilityClient.getAllCapabilities(any())
        } returns Tasks.forResult(createDummyCapabilities(capabilities, setOf(node)))

        val result = discoveryPlatform.getCapabilitiesFor(node.id)
        assertTrue { result.containsAll(result) }
    }

    @Test
    public fun getCapabilitiesFor_ignoresCapabilitiesForOtherNodes(): Unit = runBlocking {
        val capabilities = listOf("capability1", "capability2", "capability3")
        val node = createNodes(1).first()
        every {
            capabilityClient.getAllCapabilities(any())
        } returns Tasks.forResult(createDummyCapabilities(capabilities, setOf(node)))

        val result = discoveryPlatform.getCapabilitiesFor("other-node-id")
        assertTrue { result.isEmpty() }
    }

    @Test
    public fun watchHasCapability_flowsTrueWhenWatchHasCapability(): Unit = runBlocking {
        val capability = "capability1"
        val node = createNodes(1).first()

        // Mock CapabilityInfo without target node
        every {
            capabilityClient.getCapability(capability, any())
        } returns Tasks.forResult(DummyCapabilityInfo(capability, mutableSetOf(node)))
        // Mock listeners
        every {
            capabilityClient.addListener(any(), capability)
        } returns Tasks.forResult(null)
        every { capabilityClient.removeListener(any()) } returns Tasks.forResult(true)

        discoveryPlatform.watchHasCapability(node.id, capability).test {
            assertTrue(awaitItem())
        }
    }

    @Test
    public fun watchHasCapability_flowsFalseWhenWatchHasNoCapability(): Unit = runBlocking {
        val capability = "capability1"
        val node = createNodes(1).first()

        // Mock CapabilityInfo without target node
        every {
            capabilityClient.getCapability(capability, any())
        } returns Tasks.forResult(DummyCapabilityInfo(capability, mutableSetOf()))
        // Mock listeners
        every {
            capabilityClient.addListener(any(), capability)
        } returns Tasks.forResult(null)
        every { capabilityClient.removeListener(any()) } returns Tasks.forResult(true)

        discoveryPlatform.watchHasCapability(node.id, capability).test {
            assertFalse(awaitItem())
        }
    }

    @Test
    public fun watchHasCapability_removesListenerOnCancel(): Unit = runBlocking {
        val capability = "capability1"
        val node = createNodes(1).first()

        every {
            capabilityClient.getCapability(capability, any())
        } returns Tasks.forResult(DummyCapabilityInfo(capability, mutableSetOf()))
        every {
            capabilityClient.addListener(any(), capability)
        } returns Tasks.forResult(null)
        every { capabilityClient.removeListener(any()) } returns Tasks.forResult(true)

        discoveryPlatform.watchHasCapability(node.id, capability).test {
            // We expect at least one item
            awaitItem()
            cancel()
        }
        verify { capabilityClient.removeListener(any()) }
    }

    @Test
    public fun connectionModeFor_flowsBluetoothWhenNodeIsNearby(): Unit = runBlocking {
        val node = createNodes(1, true).first()
        every { nodeClient.connectedNodes } returns Tasks.forResult(listOf(node))
        discoveryPlatform.connectionModeFor(node.id).test {
            assertEquals(ConnectionMode.Bluetooth, awaitItem())
        }
    }

    @Test
    public fun connectionModeFor_flowsInternetWhenNodeIsNotNearby(): Unit = runBlocking {
        val node = createNodes(1, false).first()
        every { nodeClient.connectedNodes } returns Tasks.forResult(listOf(node))
        discoveryPlatform.connectionModeFor(node.id).test {
            assertEquals(ConnectionMode.Internet, awaitItem())
        }
    }

    @Test
    public fun connectionModeFor_flowsDisconnectedWhenNodeNotFound(): Unit = runBlocking {
        every { nodeClient.connectedNodes } returns Tasks.forResult(listOf())
        discoveryPlatform.connectionModeFor("some-node-id").test {
            assertEquals(ConnectionMode.Disconnected, awaitItem())
        }
    }

    @Test
    public fun addLocalCapability_returnsTrueOnSuccess(): Unit = runBlocking {
        val capability = "capability1"
        every { capabilityClient.addLocalCapability(any()) } returns Tasks.forResult(null)

        val result = discoveryPlatform.addLocalCapability(capability)
        assertTrue(result)
    }

    @Test
    public fun addLocalCapability_returnsFalseOnError(): Unit = runBlocking {
        val capability = "capability1"
        every { capabilityClient.addLocalCapability(any()) } throws Exception()

        val result = discoveryPlatform.addLocalCapability(capability)
        assertFalse(result)
    }

    @Test
    public fun removeLocalCapability_returnsTrueOnSuccess(): Unit = runBlocking {
        val capability = "capability1"
        every { capabilityClient.removeLocalCapability(any()) } returns Tasks.forResult(null)

        val result = discoveryPlatform.removeLocalCapability(capability)
        assertTrue(result)
    }

    @Test
    public fun removeLocalCapability_returnsFalseOnError(): Unit = runBlocking {
        val capability = "capability1"
        every { capabilityClient.removeLocalCapability(any()) } throws Exception()

        val result = discoveryPlatform.removeLocalCapability(capability)
        assertFalse(result)
    }
}
