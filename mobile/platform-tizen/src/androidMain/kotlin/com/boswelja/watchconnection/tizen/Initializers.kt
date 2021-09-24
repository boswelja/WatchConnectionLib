package com.boswelja.watchconnection.tizen

import android.content.Context
import com.samsung.android.sdk.SsdkUnsupportedException
import com.samsung.android.sdk.accessory.SA

public enum class TizenInitResult {
    DeviceUnsupported,
    VendorUnsupported,
    LibraryNotInstalled,
    LibraryUpdateRequired,
    LibraryUpdateRecommended,
    Success
}

public fun Context.initTizenAccessory(): TizenInitResult {
    val sa = SA()
    return try {
        sa.initialize(this)
        TizenInitResult.Success
    } catch (e: SsdkUnsupportedException) {
        when (e.type) {
            SsdkUnsupportedException.DEVICE_NOT_SUPPORTED ->
                TizenInitResult.DeviceUnsupported
            SsdkUnsupportedException.VENDOR_NOT_SUPPORTED ->
                TizenInitResult.VendorUnsupported
            SsdkUnsupportedException.LIBRARY_NOT_INSTALLED ->
                TizenInitResult.LibraryNotInstalled
            SsdkUnsupportedException.LIBRARY_UPDATE_IS_REQUIRED ->
                TizenInitResult.LibraryUpdateRequired
            SsdkUnsupportedException.LIBRARY_UPDATE_IS_RECOMMENDED ->
                TizenInitResult.LibraryUpdateRecommended
            else -> throw e
        }
    }
}
