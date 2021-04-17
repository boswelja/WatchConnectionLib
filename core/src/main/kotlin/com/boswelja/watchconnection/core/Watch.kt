package com.boswelja.watchconnection.core

import java.util.UUID

/**
 * The standardised Watch representation.
 * @param id A unique ID assigned to this watch. See [UUID].
 * @param name The display name of this watch.
 * @param platformId The ID of this watch assigned by it's platform. You should not need to use
 * this outside of the calling platform.
 * @param platform The platform identifier string of this watch. You shouldn't need to use this
 * outside the calling platform.
 */
data class Watch(
    val id: UUID,
    val name: String,
    val platformId: String,
    val platform: String
) {

    override fun equals(other: Any?): Boolean {
        if (other !is Watch) return super.equals(other)
        return other.id == id &&
            other.name == name &&
            other.platform == platform
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + platform.hashCode()
        return result
    }
}
