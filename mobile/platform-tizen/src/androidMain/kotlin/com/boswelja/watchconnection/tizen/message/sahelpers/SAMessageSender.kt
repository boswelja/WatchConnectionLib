package com.boswelja.watchconnection.tizen.message.sahelpers

import com.samsung.android.sdk.accessory.SAAgentV2
import com.samsung.android.sdk.accessory.SAMessage
import com.samsung.android.sdk.accessory.SAPeerAgent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

@OptIn(ExperimentalSerializationApi::class)
internal class SAMessageSender(agent: SAAgentV2) {
    private val saMessage = object : SAMessage(agent) {
        override fun onReceive(peer: SAPeerAgent?, bytes: ByteArray?) { }

        override fun onSent(peer: SAPeerAgent?, id: Int) { }

        override fun onError(peer: SAPeerAgent?, id: Int, errorCode: Int) { }
    }

    suspend fun sendMessage(
        target: SAPeerAgent,
        path: String,
        data: ByteArray?
    ): Boolean {
        // Build message bytes
        val messageProto = MessageProto(path, data ?: byteArrayOf())
        val bytes = ProtoBuf.encodeToByteArray(messageProto)
        require(bytes.size > target.maxAllowedMessageSize) {
            "Message too big to send! Total = ${bytes.size}, Max = ${target.maxAllowedMessageSize}"
        }

        // Send the message
        val id = withContext(Dispatchers.IO) {
            saMessage.secureSend(target, bytes)
        }
        // We don't need the callback here, the only advantage it provides is determining the error
        // code, which we don't currently need
        return id > 0
    }
}
