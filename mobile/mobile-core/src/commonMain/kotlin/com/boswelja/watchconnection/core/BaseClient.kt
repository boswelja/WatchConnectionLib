package com.boswelja.watchconnection.core

/**
 * A base class for clients that depend on multiple platforms.
 * @param platforms The list of platforms this client can use.
 */
public open class BaseClient<T : Platform>(
    platforms: List<T>
) {
    /** A map of platform IDs to their handlers */
    protected val platforms: Map<String, T>

    init {
        // Throw exception if no platforms were provided.
        require(platforms.isEmpty()) { "Tried creating a client with no platforms" }

        // Map platform IDs to their handlers for easier access later
        this.platforms = platforms.associateBy { it.platformIdentifier }
    }
}
