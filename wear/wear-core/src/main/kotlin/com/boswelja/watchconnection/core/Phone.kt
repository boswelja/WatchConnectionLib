package com.boswelja.watchconnection.core

import com.boswelja.watchconnection.common.Device
import java.util.UUID

open class Phone(
    name: String,
    internalId: String,
    id: UUID = uidFor(internalId)
) : Device(id, name, internalId) {
    companion object {
        fun uidFor(internalId: String): UUID = UUID.nameUUIDFromBytes(internalId.toByteArray())
    }
}
