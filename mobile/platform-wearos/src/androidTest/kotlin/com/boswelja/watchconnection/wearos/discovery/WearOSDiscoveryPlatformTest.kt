package com.boswelja.watchconnection.wearos.discovery

import android.content.Context
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.NodeClient
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

private const val TIMEOUT = 250L

public class WearOSDiscoveryPlatformTest {

    private lateinit var context: Context
    private lateinit var nodeClient: NodeClient
    private lateinit var capabilityClient: CapabilityClient
    private lateinit var discoveryPlatform: WearOSDiscoveryPlatform

    @Before
    public fun setUp() {
        context = mockk()
        nodeClient = mockk()
        capabilityClient = mockk()
        discoveryPlatform = WearOSDiscoveryPlatform(
            nodeClient,
            capabilityClient
        )
    }

    @Test
    public fun `allWatches flows initial value immediately`() {
        val nodes = createNodes(5)
        every { nodeClient.connectedNodes } returns Tasks.forResult(nodes)

        val watches = runBlocking {
            withTimeout(TIMEOUT) {
                discoveryPlatform.allWatches().first()
            }
        }

        assertEquals(nodes.map { it.id }, watches.map { it.internalId })
    }
}
