package com.boswelja.watchconnection.tizen.discovery.sahelpers

import androidx.room.Entity

@Entity(tableName = "capabilities", primaryKeys = ["peerId", "capability"])
internal data class Capability(
    val peerId: String,
    val capability: String
)
