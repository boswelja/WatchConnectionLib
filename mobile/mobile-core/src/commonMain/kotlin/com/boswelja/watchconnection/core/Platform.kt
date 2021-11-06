package com.boswelja.watchconnection.core

/**
 * Provides a common interface between platforms
 */
public interface Platform {

    /**
     * Returns a unique string to identify this platform. This will be used to map watches to the
     * correct platform as needed.
     */
    public val platformIdentifier: String
}

internal fun platformNotFoundMessage(platform: String): String =
    "Platform $platform not found! Did you forget to add it to the client?"
