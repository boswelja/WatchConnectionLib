package com.boswelja.watchconnection.wearos

import android.content.Context
import androidx.collection.ArrayMap
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.AvailabilityException
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Wearable
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

public class InitializerTest {

    private lateinit var googleApiAvailability: GoogleApiAvailability
    private lateinit var context: Context

    @Before
    public fun setUp() {
        googleApiAvailability = mockk()
        context = mockk()
        mockkStatic(GoogleApiAvailability::class)
        mockkStatic(Wearable::class)
        every { Wearable.getCapabilityClient(any<Context>()) } returns mockk()
        every { Wearable.getMessageClient(any<Context>()) } returns mockk()
        every { Wearable.getNodeClient(any<Context>()) } returns mockk()
        every { GoogleApiAvailability.getInstance() } returns googleApiAvailability
    }

    @After
    public fun tearDown() {
        unmockkAll()
    }

    @Test
    public fun isWearOSAvailable_returnsTrueWhenAllComponentsAvailable(): Unit = runBlocking {
        every {
            googleApiAvailability.checkApiAvailability(any(), any(), any())
        } returns Tasks.forResult(null)

        val result = context.isWearOSAvailable()
        assertTrue(result)
    }

    @Test
    public fun isWearOSAvailable_returnsFalseWhenAnyComponentUnavailable(): Unit = runBlocking {
        every {
            googleApiAvailability.checkApiAvailability(any(), any(), any())
        } throws AvailabilityException(ArrayMap())

        val result = context.isWearOSAvailable()
        assertFalse(result)
    }
}
