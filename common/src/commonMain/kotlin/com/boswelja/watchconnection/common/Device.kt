package com.boswelja.watchconnection.common

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom

/**
 * The standardised Device representation.
 * @param id A unique ID assigned to this device. See [Uuid].
 * @param name The display name of this device.
 * @param internalId The ID of this device assigned by the platform. You should not need to use
 * this outside of the calling platform.
 */
open class Device(
    open val id: Uuid,
    open val name: String,
    open val internalId: String
) {

    constructor(
        name: String,
        internalId: String
    ) : this(
        uuidFrom(internalId),
        name,
        internalId
    )

    override fun equals(other: Any?): Boolean {
        if (other !is Device) return super.equals(other)
        return other.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
