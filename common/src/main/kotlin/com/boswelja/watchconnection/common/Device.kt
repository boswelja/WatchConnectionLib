package com.boswelja.watchconnection.common

import java.util.UUID

/**
 * The standardised Device representation.
 * @param id A unique ID assigned to this device. See [UUID].
 * @param name The display name of this device.
 * @param internalId The ID of this device assigned by the platform. You should not need to use
 * this outside of the calling platform.
 */
open class Device(
    open val id: UUID,
    open val name: String,
    open val internalId: String
) {

    constructor(
        name: String,
        internalId: String
    ) : this(
        UUID.nameUUIDFromBytes(internalId.toByteArray(Charsets.UTF_8)),
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
