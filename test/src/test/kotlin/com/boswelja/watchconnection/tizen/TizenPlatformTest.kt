package com.boswelja.watchconnection.tizen

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.samsung.android.sdk.accessory.SAAgentV2
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockkStatic
import io.mockk.verify
import java.util.UUID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.isFailure
import strikt.assertions.isTrue

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.R])
class TizenPlatformTest {

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private lateinit var connectionHandler: TizenPlatform

    @RelaxedMockK private lateinit var accessoryAgent: TizenAccessoryAgent

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        // Inject agent mock
        mockkStatic(SAAgentV2::class)
        every {
            SAAgentV2.requestAgent(any(), TizenAccessoryAgent::class.java.name, any())
        } answers {
            this.thirdArg<SAAgentV2.RequestAgentCallback>().onAgentAvailable(accessoryAgent)
        }

        connectionHandler = TizenPlatform(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun `connectionHandler connects to agent`() {
        // Mock a successful agent init
        every {
            SAAgentV2.requestAgent(any(), TizenAccessoryAgent::class.java.name, any())
        } answers {
            this.thirdArg<SAAgentV2.RequestAgentCallback>().onAgentAvailable(accessoryAgent)
        }
        connectionHandler = TizenPlatform(ApplicationProvider.getApplicationContext())
        expectThat(connectionHandler.isReady).isTrue()
    }

    @Test
    fun `connectionHandler throws exception on callback error`() {
        // Mock an error
        every {
            SAAgentV2.requestAgent(any(), any(), any())
        } answers {
            // Expect exception here
            expectCatching {
                this.thirdArg<SAAgentV2.RequestAgentCallback>().onError(0, "Mocking error")
            }.isFailure()
        }
        TizenPlatform(ApplicationProvider.getApplicationContext())
    }

    @Test(expected = Exception::class)
    fun `connectionHandler throws exception on invalid agent provided`() {
        // Create dummy agent
        val dummyAgent = object : SAAgentV2(
            "string",
            ApplicationProvider.getApplicationContext()
        ) { }

        // Mock an error
        every {
            SAAgentV2.requestAgent(any(), any(), any())
        } answers {
            // Expect exception here
            expectCatching {
                this.thirdArg<SAAgentV2.RequestAgentCallback>().onAgentAvailable(dummyAgent)
            }.isFailure()
        }
        TizenPlatform(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun `allWatches calls agent`(): Unit = runBlocking {
        connectionHandler.allWatches().collect()
        verify { accessoryAgent.allWatches() }
    }

    @Test
    fun `watchesWithApp calls agent`(): Unit = runBlocking {
        connectionHandler.watchesWithApp().collect()
        verify { accessoryAgent.allWatches() }
    }

    @Test
    fun `getCapabilitiesFor calls agent`(): Unit = runBlocking {
        val id = "id"
        connectionHandler.getCapabilitiesFor(id).collect()
        verify { accessoryAgent.getCapabilitiesFor(id) }
    }

    @Test
    fun `sendMessage calls agent`(): Unit = runBlocking {
        val id = "id"
        val message = "message"
        val data = ByteArray(0)
        connectionHandler.sendMessage(id, message, data)
        coVerify { accessoryAgent.sendMessage(id, message, data) }
    }

    @Test
    fun `registerMessageListener calls agent`() {
        val listener = object : MessageListener {
            override fun onMessageReceived(
                sourceWatchId: UUID,
                message: String,
                data: ByteArray?
            ) { }
        }
        connectionHandler.addMessageListener(listener)
        verify { accessoryAgent.registerMessageListener(listener) }
    }

    @Test
    fun `unregisterMessageListener calls agent`() {
        val listener = object : MessageListener {
            override fun onMessageReceived(
                sourceWatchId: UUID,
                message: String,
                data: ByteArray?
            ) { }
        }
        connectionHandler.removeMessageListener(listener)
        verify { accessoryAgent.unregisterMessageListener(listener) }
    }
}
