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
 */
public data class Watch(
    public val uid: String,
    public val name: String
) {

    val internalId: String
    val platform: String

    public constructor(
        name: String,
        internalId: String,
        platform: String
    ) : this(
        createUid(platform, internalId),
        name
    )

    init {
        val (platform, internalId) = uid.split("|")
        this.internalId = internalId
        this.platform = platform
    }

    internal companion object {

        /**
         * Gets a reproducible, unique ID from the provided info.
         * @param platform The platform identifier for this watches platform.
         * @param platformId See [Watch.internalId].
         */
        fun createUid(platform: String, platformId: String): String = "$platform|$platformId"
    }
}
