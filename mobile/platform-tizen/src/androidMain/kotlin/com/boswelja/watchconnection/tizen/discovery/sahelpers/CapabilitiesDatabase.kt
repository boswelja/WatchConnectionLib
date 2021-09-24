package com.boswelja.watchconnection.tizen.discovery.sahelpers

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.map

internal fun Context.getCapabilitiesDatabase(): CapabilitiesDatabase {
    return Room.databaseBuilder(
        applicationContext,
        CapabilitiesDatabase::class.java,
        "watchconnectionlib-capabilities"
    ).enableMultiInstanceInvalidation().build()
}

@Database(entities = [Capability::class], version = 1)
internal abstract class CapabilitiesDatabase : RoomDatabase() {
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
     * Flow whether a watch with a given internal ID has the given capability.
     * @param peerId The internal ID of the watch.
     * @param capability The capability to check.
     */
    fun hasCapability(peerId: String, capability: String): Flow<Boolean> = capabilitiesDao()
        .getCapability(peerId, capability)
        .map { it != null }

    /**
     * Flow a list of capability strings for a watch with a given internal ID.
     * @param peerId The internal ID of the watch to collect capabilities for.
     */
    fun getCapabilitiesFor(peerId: String): Flow<Set<String>> = capabilitiesDao()
        .getCapabilitiesById(peerId)
        .conflate()
        .map { capabilities -> capabilities.map { it.capability }.toSet() }

    /**
     * Flow a list of watch internal IDs that declare a given capability.
     * @param capability The capability to collect declaring watches for.
     */
    fun getPeersWithCapability(capability: String): Flow<Set<String>> = capabilitiesDao()
        .getCapabilitiesByCapability(capability)
        .conflate()
        .map { capabilities -> capabilities.map { it.peerId }.toSet() }
}
