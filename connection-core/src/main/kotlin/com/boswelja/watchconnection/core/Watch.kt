package com.boswelja.watchconnection.core

import java.util.UUID

data class Watch(
    val id: UUID,
    val name: String,
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
