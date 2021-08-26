package com.boswelja.watchconnection.core

import java.util.UUID

open class Phone(
    open val id: UUID,
    open val name: String,
    open val internalId: String
)

fun uidFor(internalId: String): UUID = UUID.nameUUIDFromBytes(internalId.toByteArray())
