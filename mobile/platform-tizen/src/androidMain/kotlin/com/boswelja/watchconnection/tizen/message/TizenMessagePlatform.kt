package com.boswelja.watchconnection.tizen.message

import android.content.Context
import com.boswelja.watchconnection.common.message.Message
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.boswelja.watchconnection.core.message.MessagePlatform
import com.boswelja.watchconnection.tizen.Constants.TIZEN_PLATFORM
import com.boswelja.watchconnection.tizen.TizenAccessoryAgent
import com.boswelja.watchconnection.tizen.getTizenAccessoryAgent
import com.boswelja.watchconnection.tizen.message.sahelpers.SAMessageSender
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

public actual class TizenMessagePlatform(
    private val context: Context
) : MessagePlatform() {

    override val platformIdentifier: String = TIZEN_PLATFORM

    private var accessoryAgent: TizenAccessoryAgent? = null
    public var isReady: Boolean = false
        private set

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun incomingMessages(): Flow<ReceivedMessage<ByteArray?>> = flow {
        ensureAccessoryAgentLoaded()
        val mappedFlow = accessoryAgent!!.incomingMessages()
            .map { (peer, message) ->
                val bytes = if (message.data.isNotEmpty()) message.data else null
                ReceivedMessage(
                    peer.peerId,
                    message.path,
                    bytes
                )
            }
        emitAll(mappedFlow)
    }

    override suspend fun sendMessage(
        watchId: String,
        message: String,
        data: ByteArray?,
        priority: Message.Priority
    ): Boolean {
        ensureAccessoryAgentLoaded()
        val peer = accessoryAgent?.getPeerFromID(watchId)
        checkNotNull(peer) { "No watch found with the given ID" }
        val messageSender = SAMessageSender(accessoryAgent!!)
        return messageSender.sendMessage(
            peer,
            message,
            data
        )
    }

    private suspend fun ensureAccessoryAgentLoaded() {
        if (accessoryAgent == null) {
            accessoryAgent = context.getTizenAccessoryAgent()
        }
        checkNotNull(accessoryAgent) { "TizenAccessoryAgent failed to initialize" }
    }
}
