package com.boswelja.watchconnection.core

/**
 * Provides a common interface between clients
 */
interface Platform {

    /**
     * Returns a unique string to identify this platform. This will be used to map watches to the
     * correct platform as needed.
     */
    val platformIdentifier: String
}
