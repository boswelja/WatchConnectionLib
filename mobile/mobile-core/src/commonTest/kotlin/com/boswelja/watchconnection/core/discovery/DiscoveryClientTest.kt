package com.boswelja.watchconnection.core.discovery

import app.cash.turbine.test
import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.common.discovery.ConnectionMode
import com.boswelja.watchconnection.core.createCapabilities
import com.boswelja.watchconnection.core.createWatchesFor
import com.boswelja.watchconnection.core.runBlockingTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class DiscoveryClientTest {

    private lateinit var platforms: List<ConcreteDiscoveryPlatform>
    private lateinit var client: DiscoveryClient

    @BeforeTest
    fun setUp() {
        platforms = createPlatforms(3)
        client = DiscoveryClient(platforms)
    }

    @Test
    fun allWatches_flowsFromAllPlatforms() = runBlockingTest {
        // Set up watches
        val perPlatformCount = 5
        val totalCount = perPlatformCount * platforms.count()
        platforms.forEach {
            it.allWatches.addAll(createWatchesFor(perPlatformCount, it.platformIdentifier))
        }

        // Check the result
        client.allWatches().test {
            val watches = awaitItem()
            assertEquals(totalCount, watches.count())
            platforms.forEach { platform ->
                assertEquals(
                    perPlatformCount,
                    watches.filter { it.platform == platform.platformIdentifier }.count()
                )
            }
        }
    }

    @Test
    fun getCapabilitiesFor_throwsWhenPlatformNotFound() = runBlockingTest {
        val targetUid = Watch.createUid("some-invalid-platform", "id")
        assertFails {
            client.getCapabilitiesFor(targetUid)
        }
    }

    @Test
    fun getCapabilitiesFor_callsCorrespondingPlatform() = runBlockingTest {
        val capabilities = createCapabilities(5)
        val platform = platforms.first()
        platform.capabilities.addAll(capabilities)

        val targetUid = Watch.createUid(platform.platformIdentifier, "id")
        assertEquals(capabilities, client.getCapabilitiesFor(targetUid))
    }

    @Test
    fun hasCapability_throwsWhenPlatformNotFound() = runBlockingTest {
        val targetUid = Watch.createUid("some-invalid-platform", "id")
        assertFails {
            client.hasCapability(targetUid, "capability")
        }
    }

    @Test
    fun hasCapability_callsCorrespondingPlatform() = runBlockingTest {
        val capability = "capability"
        val platform = platforms.first()
        platform.capabilities.add(capability)

        val targetUid = Watch.createUid(platform.platformIdentifier, "id")
        client.hasCapability(targetUid, capability).test {
            assertTrue(awaitItem())
        }
    }

    @Test
    fun connectionModeFor_throwsWhenPlatformNotFound() = runBlockingTest {
        val targetUid = Watch.createUid("some-invalid-platform", "id")
        assertFails {
            client.connectionModeFor(targetUid)
        }
    }

    @Test
    fun connectionModeFor_callsCorrespondingPlatform() = runBlockingTest {
        val connectionMode = ConnectionMode.Internet
        val platform = platforms.first()
        platform.connectionMode = connectionMode

        val targetUid = Watch.createUid(platform.platformIdentifier, "id")
        client.connectionModeFor(targetUid).test {
            assertEquals(connectionMode, awaitItem())
        }
    }

    @Test
    fun addLocalCapability_callsAllPlatforms() = runBlockingTest {
        // Reset data
        platforms.forEach { it.localCapabilities.clear() }

        // Add a capability
        val capability = "new-capability"
        client.addLocalCapability(capability)

        // Check the result
        platforms.forEach {
            assertTrue { it.localCapabilities.contains(capability) }
        }
    }

    @Test
    fun addLocalCapability_returnsFalseOnNoChanges() = runBlockingTest {
        val capability = "new-capability"

        // Reset data
        platforms.forEach { it.localCapabilities.add(capability) }

        // Add a capability
        val result = client.addLocalCapability(capability)

        // Check the result
        assertFalse(result)
    }

    @Test
    fun removeLocalCapability_callsAllPlatforms() = runBlockingTest {
        val capability = "new-capability"

        // Reset data
        platforms.forEach { it.localCapabilities.add(capability) }

        // Remove a capability
        client.removeLocalCapability(capability)

        // Check the result
        platforms.forEach {
            assertFalse { it.localCapabilities.contains(capability) }
        }
    }

    @Test
    fun removeLocalCapability_returnsFalseOnNoChanges() = runBlockingTest {
        val capability = "new-capability"

        // Reset data
        platforms.forEach { it.localCapabilities.clear() }

        // Add a capability
        val result = client.removeLocalCapability(capability)

        // Check the result
        assertFalse(result)
    }
}
