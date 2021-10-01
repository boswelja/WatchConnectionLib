package com.boswelja.watchconnection.tizen.discovery.sahelpers

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface CapabilitiesDao {
    @Query("SELECT * FROM capabilities WHERE peerId = :peerId")
    suspend fun getCapabilitiesById(peerId: String): List<Capability>

    @Query("SELECT * FROM capabilities where capability = :capability")
    fun getCapabilitiesByCapability(capability: String): Flow<List<Capability>>

    @Query("SELECT * FROM capabilities where peerId = :peerId AND capability = :capability")
    fun getCapability(peerId: String, capability: String): Flow<Capability?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCapability(capability: Capability)

    @Delete
    suspend fun removeCapability(capability: Capability)
}
