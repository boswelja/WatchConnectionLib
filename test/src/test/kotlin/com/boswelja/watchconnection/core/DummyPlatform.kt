package com.boswelja.watchconnection.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class DummyPlatform(
    override val platformIdentifier: String,
    val allWatches: Array<Watch>,
    val watchesWithApp: Array<Watch>
) : WatchPlatform {

    override fun allWatches(): Flow<Watch> = flowOf(*allWatches)

    override fun watchesWithApp(): Flow<Watch> = flowOf(*watchesWithApp)

    override fun getCapabilitiesFor(watchId: String): Flow<String> = flow { }

    override fun getStatusFor(watchId: String): Flow<Status> = flow { }

    override suspend fun sendMessage(watchId: String, message: String, data: ByteArray?): Boolean {
        // Do nothing
        return false
    }

    override fun addMessageListener(listener: MessageListener) {
        // Do nothing
    }

    override fun removeMessageListener(listener: MessageListener) {
        // Do nothing
    }
}
