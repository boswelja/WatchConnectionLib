package com.boswelja.watchconnection.wearos

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.boswelja.watchconnection.core.Messages
import com.boswelja.watchconnection.wearos.CapabilityHelpers.createCapabilities
import com.boswelja.watchconnection.wearos.NodeHelpers.createDummyNodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.NodeClient
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import strikt.api.expectThat
import strikt.assertions.isContainedIn
import strikt.assertions.isFalse
import strikt.assertions.isTrue
import java.util.UUID
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.R])
class WearOSPlatformTest {

    private val appCapability = "app-capability"
    private val capabilities = createCapabilities(5)

    private lateinit var connectionHandler: WearOSPlatform

    @RelaxedMockK private lateinit var nodeClient: NodeClient
    @RelaxedMockK private lateinit var messageClient: MessageClient
    @RelaxedMockK private lateinit var capabilityClient: CapabilityClient

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        connectionHandler = WearOSPlatform(
            appCapability,
            capabilities,
            nodeClient,
            messageClient,
            capabilityClient
        )
    }

    @Test
    fun `allWatches flows from connectedNodes`(): Unit = runBlocking {
        // Create dummy nodes and mock them
        val dummyNodes = createDummyNodes(3)
        every { nodeClient.connectedNodes } returns Tasks.forResult(dummyNodes)

        // Collect the result and make sure they match up with dummy nodes
        connectionHandler.allWatches().collect { watch ->
            expectThat(dummyNodes.any { it.id == watch.platformId && it.displayName == watch.name })
                .isTrue()
        }
    }

    @Test
    fun `watchesWithApp flows from capability client`(): Unit = runBlocking {
        // Create dummy CapabilityInfo and set up mock
        val dummyNodes = createDummyNodes(3)
        every {
            capabilityClient.getCapability(appCapability, any())
        } returns Tasks.forResult(
            CapabilityHelpers.DummyCapabilityInfo(
                appCapability,
                dummyNodes.toMutableSet()
            )
        )

        // Collect the result and make sure they match up with dummy nodes
        connectionHandler.watchesWithApp().collect { watch ->
            expectThat(dummyNodes.any { it.id == watch.platformId && it.displayName == watch.name })
                .isTrue()
        }
    }

    @Test
    fun `getCapabilitiesFor flows correct capabilities when watch has all`(): Unit = runBlocking {
        // Create dummy CapabilityInfos and set up mocks
        val dummyNode = createDummyNodes(1)
        capabilities.forEach { capability ->
            every {
                capabilityClient.getCapability(capability, any())
            } returns Tasks.forResult(
                CapabilityHelpers.DummyCapabilityInfo(
                    appCapability,
                    dummyNode.toMutableSet()
                )
            )
        }

        // Collect the result and make sure they match up with capabilities
        connectionHandler.getCapabilitiesFor(dummyNode.first().id).collect { capability ->
            expectThat(capability).isContainedIn(capabilities)
        }
    }

    @Test
    fun `sendMessage returns true on success`(): Unit = runBlocking {
        // Set up constants
        val id = "id"
        val message = "message"
        val data = ByteArray(0)

        // Mock messageClient to error on sendMessage
        every {
            messageClient.sendMessage(any(), any(), any())
        } returns Tasks.forResult(1)

        // Call sendMessage and check result
        val result = connectionHandler.sendMessage(id, message, data)
        expectThat(result).isTrue()
    }

    @Test
    fun `sendMessage calls messageClient`(): Unit = runBlocking {
        // Set up constants
        val id = "id"
        val message = "message"
        val data = ByteArray(0)

        // Mock messageClient to error on sendMessage
        every {
            messageClient.sendMessage(any(), any(), any())
        } throws ApiException(Status.RESULT_CANCELED)

        // Call sendMessage and check result
        val result = connectionHandler.sendMessage(id, message, data)
        expectThat(result).isFalse()
    }

    @Test
    fun `registerMessageListener registers a listener with messageClient`() {
        // Create dummy listener
        val listener = object : Messages.Listener {
            override fun onMessageReceived(
                sourceWatchId: UUID,
                message: String,
                data: ByteArray?
            ) { }
        }

        // Register listener
        connectionHandler.addMessageListener(listener)

        // Check messageClient was called
        verify { messageClient.addListener(any()) }
    }

    @Test
    fun `unregisterMessageListener removes a listener with messageClient`() {
        // Create dummy listener
        val listener = object : Messages.Listener {
            override fun onMessageReceived(
                sourceWatchId: UUID,
                message: String,
                data: ByteArray?
            ) { }
        }

        // We need to add a listener first
        connectionHandler.addMessageListener(listener)

        // Register listener
        connectionHandler.removeMessageListener(listener)

        // Check messageClient was called
        verify { messageClient.removeListener(any()) }
    }

    @Test
    fun `unregisterMessageListener does nothing with previously unregistered listener`() {
        // Create dummy listener
        val listener = object : Messages.Listener {
            override fun onMessageReceived(
                sourceWatchId: UUID,
                message: String,
                data: ByteArray?
            ) { }
        }

        // Register listener
        connectionHandler.removeMessageListener(listener)

        // Check messageClient was called
        verify(inverse = true) { messageClient.removeListener(any()) }
    }
}
