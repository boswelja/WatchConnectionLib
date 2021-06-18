package com.boswelja.watchconnection.wearos.rules

import com.google.android.gms.wearable.Node

class DummyNode(
    private val displayName: String,
    private val id: String
) : Node {

    override fun getDisplayName(): String = displayName

    override fun getId(): String = id

    override fun isNearby(): Boolean = true
}

fun createNodes(count: Int): List<Node> {
    return (0..count).map {
        DummyNode(
            it.toString(),
            it.toString()
        )
    }
}
