package com.boswelja.watchconnection.tizen

import android.content.Context
import com.boswelja.watchconnection.tizen.message.sahelpers.MessageProto
import com.samsung.android.sdk.accessory.SAAgentV2
import com.samsung.android.sdk.accessory.SAMessage
import com.samsung.android.sdk.accessory.SAPeerAgent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf

private const val ProviderAgentName = "TizenAccessoryProvider"

/**
 * Get an instance of [TizenAccessoryAgent], or null if there was an error.
 */
internal suspend fun Context.getTizenAccessoryAgent(): TizenAccessoryAgent? {
    val channel = Channel<TizenAccessoryAgent?>()
    val callback = object : SAAgentV2.RequestAgentCallback {
        override fun onAgentAvailable(agent: SAAgentV2?) {
            channel.trySend(agent as TizenAccessoryAgent)
            channel.close()
        }

        override fun onError(errorCode: Int, message: String?) {
            channel.trySend(null)
            channel.close()
        }
    }
    SAAgentV2.requestAgent(
        this.applicationContext,
        TizenAccessoryAgent::class.java.name,
        callback
    )
    return channel.receive()
}

internal class TizenAccessoryAgent(
    context: Context
) : SAAgentV2(ProviderAgentName, context) {

    private val _foundPeerAgents = MutableStateFlow<Set<SAPeerAgent>>(setOf())
    val foundPeerAgents: Flow<Set<SAPeerAgent>>
        get() = _foundPeerAgents

    init {
        findPeerAgents()
    }

    override fun onFindPeerAgentsResponse(peers: Array<out SAPeerAgent>?, result: Int) {
        peers?.let {
            _foundPeerAgents.update { it + peers }
        }
    }

    override fun onPeerAgentsUpdated(peers: Array<out SAPeerAgent>?, result: Int) {
        peers?.let {
            when (result) {
                PEER_AGENT_AVAILABLE -> {
                    _foundPeerAgents.update { it + peers }
                }
                PEER_AGENT_UNAVAILABLE -> {
                    _foundPeerAgents.update { it - peers }
                }
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class, ExperimentalCoroutinesApi::class)
    fun incomingMessages() = callbackFlow {
        object : SAMessage(this@TizenAccessoryAgent) {
            override fun onReceive(peer: SAPeerAgent?, messageBytes: ByteArray?) {
                requireNotNull(peer) { "Message from unknown peer" }
                requireNotNull(messageBytes) { "No message data received" }
                val messageProto = ProtoBuf.decodeFromByteArray<MessageProto>(messageBytes)
                trySend(Pair(peer, messageProto))
            }

            override fun onSent(p0: SAPeerAgent?, p1: Int) { }
            override fun onError(p0: SAPeerAgent?, p1: Int, p2: Int) { }
        }
    }

    suspend fun getPeerFromID(id: String): SAPeerAgent? {
        return foundPeerAgents.first().firstOrNull { it.peerId == id }
    }
}
