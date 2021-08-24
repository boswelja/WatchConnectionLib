package com.boswelja.watchconnection.wearos.rules

import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.Node

class DummyCapabilityInfo(
    private val name: String,
    private val nodes: List<Node>
) : CapabilityInfo {

    override fun getName(): String = name

    override fun getNodes(): MutableSet<Node> = nodes.toMutableSet()
}
