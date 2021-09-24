package com.boswelja.watchconnection.tizen.message.sahelpers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

/**
 * A serializable class that represents a message on the Tizen platform.
 * @param path THe message path.
 * @param data The serialized message data.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
internal data class MessageProto(
    @ProtoNumber(1)
    val path: String,
    @ProtoNumber(2)
    val data: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MessageProto

        if (path != other.path) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}
