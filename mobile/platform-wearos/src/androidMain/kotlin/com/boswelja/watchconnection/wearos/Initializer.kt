package com.boswelja.watchconnection.wearos

import android.content.Context
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.AvailabilityException
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.tasks.await

/**
 * Check whether Wear OS APIs are available.
 * @return true if all Wear OS APIs are available, false otherwise
 */
public suspend fun Context.isWearOSAvailable(): Boolean {
    val availabilityChecker = GoogleApiAvailability.getInstance()
    return try {
        availabilityChecker.checkApiAvailability(
            Wearable.getCapabilityClient(this),
            Wearable.getMessageClient(this),
            Wearable.getNodeClient(this)
        ).await()
        true
    } catch (_: AvailabilityException) {
        false
    }
}
