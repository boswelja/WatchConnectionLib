package com.boswelja.watchconnection.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class DummyPlatform(
    override val platformIdentifier: String,
    val allWatches: List<Watch>,
    val watchesWithApp: List<Watch>
) : WatchPlatform {

    override val incomingMessages: Flow<Message> = flow { }

    override fun allWatches(): Flow<List<Watch>> = flowOf(allWatches)

    override fun watchesWithApp(): Flow<List<Watch>> = flowOf(watchesWithApp)

    override fun getCapabilitiesFor(watchId: String): Flow<List<String>> = flow { }

    override fun getStatusFor(watchId: String): Flow<Status> = flow { }

    override suspend fun sendMessage(watchId: String, message: String, data: ByteArray?): Boolean {
        // Do nothing
        return false
    }
}
