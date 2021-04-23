package com.boswelja.watchconnection.core

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.coVerify
import io.mockk.spyk
import io.mockk.verify
import java.util.UUID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.R])
class WatchConnectionClientTest {
    private val dummyWatchCountPerPlatform = 5
    private val dummyWatchWithAppCountPerPlatform = 3

    private val dummyPlatformIds = arrayOf("platform1", "platform2")

    private var dummyWatches: Array<Watch> = emptyArray()
    private var dummyWatchesWithApp: Array<Watch> = emptyArray()
    private var dummyPlatforms: Array<PlatformConnectionHandler> = emptyArray()

    private lateinit var connectionClient: WatchConnectionClient

    @Before
    fun setUp() {
        // Clear all existing stuff
        dummyWatches = emptyArray()
        dummyWatchesWithApp = emptyArray()
        dummyPlatforms = emptyArray()

        dummyPlatformIds.forEach { platform ->
            // Generate dummy watches
            var allWatches = emptyArray<Watch>()
            (1..dummyWatchCountPerPlatform + 1).forEach { count ->
                allWatches += Watch(
                    name = "Watch $count",
                    platformId = "watch-$count",
                    platform = platform
                )
            }

            // Generate dummy watches with app
            var watchesWithApp = emptyArray<Watch>()
            (1..dummyWatchWithAppCountPerPlatform + 1).forEach { count ->
                watchesWithApp += Watch(
                    name = "Watch $count",
                    platformId = "watch-$count",
                    platform = platform
                )
            }

            // Add to totals
            dummyWatches += allWatches
            dummyWatchesWithApp += watchesWithApp

            // Create dummy connection handlers
            dummyPlatforms += spyk(DummyConnectionHandler(platform, allWatches, watchesWithApp))
        }

        connectionClient = WatchConnectionClient(*dummyPlatforms)
    }

    @Test
    fun `allWatches gets all watches from platforms`() {
        connectionClient.allWatches()
        dummyPlatforms.forEach { verify { it.allWatches() } }
    }

    @Test
    fun `watchesWithApp gets all watches with app from platforms`() {
        connectionClient.watchesWithApp()
        dummyPlatforms.forEach { verify { it.watchesWithApp() } }
    }

    @Test
    fun `sendMessage passes request to the correct platform`(): Unit = runBlocking {
        val message = "message"
        dummyWatches.forEach { watch ->
            connectionClient.sendMessage(
                watch,
                message = message
            )
            val platform = dummyPlatforms.first { it.platformIdentifier == watch.platform }
            coVerify {
                platform.sendMessage(watch.platformId, message, null)
            }
        }
    }

    @Test
    fun `getCapabilitiesFor passes request to the correct platform`(): Unit = runBlocking {
        dummyWatches.forEach { watch ->
            connectionClient.getCapabilitiesFor(watch)
            val platform = dummyPlatforms.first { it.platformIdentifier == watch.platform }
            coVerify { platform.getCapabilitiesFor(watch.platformId) }
        }
    }

    @Test
    fun `registerMessageListener adds the message listener to all platforms`() {
        val messageListener = object : MessageListener {
            override fun onMessageReceived(
                sourceWatchId: UUID,
                message: String,
                data: ByteArray?
            ) { }
        }
        connectionClient.addMessageListener(messageListener)
        dummyPlatforms.forEach { platform ->
            verify { platform.addMessageListener(messageListener) }
        }
    }

    @Test
    fun `unregisterMessageListener removes the message listener from all platforms`() {
        val messageListener = object : MessageListener {
            override fun onMessageReceived(
                sourceWatchId: UUID,
                message: String,
                data: ByteArray?
            ) { }
        }
        connectionClient.removeMessageListener(messageListener)
        dummyPlatforms.forEach { platform ->
            verify { platform.removeMessageListener(messageListener) }
        }
    }
}
