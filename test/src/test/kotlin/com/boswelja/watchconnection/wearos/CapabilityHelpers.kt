package com.boswelja.watchconnection.wearos

import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.Node

object CapabilityHelpers {
    class DummyCapabilityInfo(
        private val name: String,
        private val nodes: MutableSet<Node>
    ) : CapabilityInfo {
        override fun getName(): String = name
        override fun getNodes(): MutableSet<Node> = nodes
    }

    fun createCapabilities(count: Int): List<String> =
        (1..(count + 1)).map { "capability-$it" }
}
