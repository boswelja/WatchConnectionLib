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

    /**
     * The watch internal ID for use with it's designated platform. There should be no references
     * to this in user code.
     */
    val internalId: String

    /**
     * The identifier for the platform this watch belongs to.
     */
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
        val (platform, internalId) = getInfoFromUid(uid)
        this.internalId = internalId
        this.platform = platform
    }

    public companion object {

        private const val UidDelimiter = "|"

        /**
         * Get a pair containing the device platform and internal ID from the given UID.
         */
        public fun getInfoFromUid(uid: String): Pair<String, String> {
            val (platform, internalId) = uid.split(UidDelimiter)
            return Pair(platform, internalId)
        }

        /**
         * Gets a reproducible, unique ID from the provided info.
         * @param platform The platform identifier for this watches platform.
         * @param platformId See [Watch.internalId].
         */
        public fun createUid(platform: String, platformId: String): String {
            require(platform.isNotBlank()) { "platform cannot be blank or empty" }
            require(platformId.isNotBlank()) { "platformId cannot be blank or empty" }
            return "$platform$UidDelimiter$platformId"
        }
    }
}
