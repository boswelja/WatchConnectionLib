package com.boswelja.watchconnection.tizen.discovery.sahelpers

import androidx.room.Database
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.map

@Database(entities = [Capability::class], version = 1)
internal abstract class CapabilitiesDatabase {
    abstract fun capabilitiesDao(): CapabilitiesDao

    /**
     * Add a capability to the database.
     * @param peerId The internal ID of the watch that declares the capability.
     * @param capability The capability string declared by the watch.
     */
    suspend fun addCapability(peerId: String, capability: String) {
        capabilitiesDao().addCapability(Capability(peerId, capability))
    }

    /**
     * Remove a capability from the database.
     * @param peerId The internal ID of the watch that removed the capability.
     * @param capability The capability string removed by the watch.
     */
    suspend fun removeCapability(peerId: String, capability: String) {
        capabilitiesDao().removeCapability(Capability(peerId, capability))
    }

    /**
     * Flow a list of capability strings for a watch with a given internal ID.
     * @param peerId The internal ID of the watch to collect capabilities for.
     */
    fun getCapabilitiesFor(peerId: String): Flow<List<String>> = capabilitiesDao()
        .getCapabilitiesById(peerId)
        .conflate()
        .map { capabilities -> capabilities.map { it.capability } }

    /**
     * Flow a list of watch internal IDs that declare a given capability.
     * @param capability The capability to collect declaring watches for.
     */
    fun getPeersWithCapability(capability: String): Flow<List<String>> = capabilitiesDao()
        .getCapabilitiesByCapability(capability)
        .conflate()
        .map { capabilities -> capabilities.map { it.peerId } }
}
