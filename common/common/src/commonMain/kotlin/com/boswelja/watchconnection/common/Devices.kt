package com.boswelja.watchconnection.common

/**
 * The standardised Phone representation.
 * @param uid A unique ID assigned to this phone.
 * @param name The display name of this phone.
 */
public data class Phone(
    public val uid: String,
    public val name: String,
)

/**
 * The standardised Watch representation.
 * @param uid A unique ID assigned to this watch.
 * @param name The display name of this watch.
 * @param internalId The ID of this watch assigned by it's platform. You should not need to use
 * this outside of the calling platform.
 * @param platform The platform identifier string of this watch. You should not need to use this
 * outside the calling platform.
 */
public data class Watch(
    public val uid: String,
    public val name: String,
    public val internalId: String,
    public val platform: String
) {

    public constructor(
        name: String,
        internalId: String,
        platform: String
    ) : this(
        createUUID(platform, internalId),
        name,
        internalId,
        platform
    )

    internal companion object {

        /**
         * Gets a reproducible, unique ID from the provided info.
         * @param platform The platform identifier for this watches platform.
         * @param platformId See [Watch.internalId].
         */
        fun createUUID(platform: String, platformId: String): String = platform + platformId
    }
}
