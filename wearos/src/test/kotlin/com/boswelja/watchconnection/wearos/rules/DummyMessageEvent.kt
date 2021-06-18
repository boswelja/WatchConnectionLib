package com.boswelja.watchconnection.wearos.rules

import com.google.android.gms.wearable.MessageEvent

class DummyMessageEvent(
    private val sourceNodeId: String,
    private val message: String,
    private val data: ByteArray? = null
) : MessageEvent {

    override fun getSourceNodeId(): String = sourceNodeId

    override fun getPath(): String = message

    override fun getData(): ByteArray? = data

    override fun getRequestId(): Int = 0
}
