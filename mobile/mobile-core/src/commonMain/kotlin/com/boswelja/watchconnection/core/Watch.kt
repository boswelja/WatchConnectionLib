package com.boswelja.watchconnection.core

import com.boswelja.watchconnection.common.Device

/**
 * The standardised Watch representation.
 * @param uid A unique ID assigned to this watch.
 * @param name The display name of this watch.
 * @param internalId The ID of this watch assigned by it's platform. You should not need to use
 * this outside of the calling platform.
 * @param platform The platform identifier string of this watch. You shouldn't need to use this
 * outside the calling platform.
 */
open class Watch(
    override val uid: String,
    override val name: String,
    val internalId: String,
    open val platform: String
) : Device(uid, name) {

    constructor(
        name: String,
        internalId: String,
        platform: String
    ) : this(
        createUUID(platform, internalId),
        name,
        internalId,
        platform
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        if (!super.equals(other)) return false

        other as Watch

        if (uid != other.uid) return false
        if (name != other.name) return false
        if (internalId != other.internalId) return false
        if (platform != other.platform) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + uid.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + internalId.hashCode()
        result = 31 * result + platform.hashCode()
        return result
    }

    companion object {

        /**
         * Gets a reproducible, unique ID from the provided info.
         * @param platform See [Platform.platformIdentifier].
         * @param platformId See [Watch.internalId].
         */
        fun createUUID(platform: String, platformId: String): String = platform + platformId
    }
}
