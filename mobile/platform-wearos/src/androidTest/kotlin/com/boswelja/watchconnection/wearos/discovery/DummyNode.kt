package com.boswelja.watchconnection.wearos.discovery

import com.google.android.gms.wearable.Node

internal class DummyNode(
    private val id: String,
    private val name: String,
    private val isNearby: Boolean
) : Node {
    override fun getId(): String = id
    override fun getDisplayName(): String = name
    override fun isNearby(): Boolean = isNearby
}

internal fun createNodes(count: Int, isNearby: Boolean = true): List<Node> {
    return (0 until count).map {
        DummyNode(
            it.toString(),
            it.toString(),
            isNearby
        )
    }
}
