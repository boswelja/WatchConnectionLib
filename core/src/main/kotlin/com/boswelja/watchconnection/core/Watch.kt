package com.boswelja.watchconnection.core

import android.os.Parcelable
import java.util.UUID
import kotlinx.parcelize.Parcelize

/**
 * The standardised Watch representation.
 * @param id A unique ID assigned to this watch. See [UUID].
 * @param name The display name of this watch.
 * @param platformId The ID of this watch assigned by it's platform. You should not need to use
 * this outside of the calling platform.
 * @param platform The platform identifier string of this watch. You shouldn't need to use this
 * outside the calling platform.
 */
@Parcelize
open class Watch(
    open val id: UUID,
    open val name: String,
    open val platformId: String,
    open val platform: String
) : Parcelable {

    constructor(
        name: String,
        platformId: String,
        platform: String
    ) : this(
        createUUID(platform, platformId),
        name,
        platformId,
        platform
    )

    override fun equals(other: Any?): Boolean {
        if (other !is Watch) return super.equals(other)
        return other.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {

        /**
         * Gets a reproducible [UUID] from the provided info.
         * @param platform See [Platform.platformIdentifier].
         * @param platformId See [Watch.platformId].
         */
        fun createUUID(platform: String, platformId: String): UUID =
            UUID.nameUUIDFromBytes((platform + platformId).toByteArray(Charsets.UTF_8))
    }
}
