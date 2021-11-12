package com.boswelja.watchconnection.wearos.discovery

import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.Node

public data class DummyCapabilityInfo(
    private val name: String,
    private val nodes: MutableSet<Node>
) : CapabilityInfo {
    override fun getName(): String = name
    override fun getNodes(): MutableSet<Node> = nodes
}

public fun createDummyCapabilities(
    capabilities: List<String>,
    nodes: Set<Node>
): Map<String, CapabilityInfo> {
    return capabilities.associateWith { DummyCapabilityInfo(it, nodes.toMutableSet()) }
}
