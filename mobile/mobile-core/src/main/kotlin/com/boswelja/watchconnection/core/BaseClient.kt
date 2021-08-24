package com.boswelja.watchconnection.core

abstract class BaseClient<T : Platform>(
    platforms: List<T>
) {
    /** A map of platform IDs to their handlers */
    protected val platforms = HashMap<String, T>()

    init {
        // Throw exception if no platforms were provided.
        if (platforms.isEmpty())
            throw IllegalArgumentException("Tried creating a client with no platforms")

        // Map platform IDs to their handlers for easier access later
        platforms.forEach {
            this.platforms[it.platformIdentifier] = it
        }
    }
}
