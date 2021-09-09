package com.boswelja.watchconnection.core

import android.os.Parcelable
import com.boswelja.watchconnection.common.Device
import java.util.UUID
import kotlinx.parcelize.Parcelize

/**
 * The standardised Watch representation.
 * @param uid A unique ID assigned to this watch.
 * @param name The display name of this watch.
 * @param internalId The ID of this watch assigned by it's platform. You should not need to use
 * this outside of the calling platform.
 * @param platform The platform identifier string of this watch. You shouldn't need to use this
 * outside the calling platform.
 */
@Parcelize
open class Watch(
    override val uid: String,
    override val name: String,
    val internalId: String,
    open val platform: String
) : Parcelable, Device(uid, name) {

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
        if (other !is Watch) return super.equals(other)
        return other.uid == uid
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }

    companion object {

        /**
         * Gets a reproducible [UUID] from the provided info.
         * @param platform See [Platform.platformIdentifier].
         * @param platformId See [Watch.internalId].
         */
        fun createUUID(platform: String, platformId: String): String = platform + platformId
    }
}
