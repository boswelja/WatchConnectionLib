package com.boswelja.watchconnection.wearos.message

import com.google.android.gms.wearable.MessageEvent

class DummyMessageEvent(
    private val sourceNodeId: String,
    private val message: String,
    private val data: ByteArray = ByteArray(0)
) : MessageEvent {

    override fun getSourceNodeId(): String = sourceNodeId

    override fun getPath(): String = message

    override fun getData(): ByteArray = data

    override fun getRequestId(): Int = 0
}
