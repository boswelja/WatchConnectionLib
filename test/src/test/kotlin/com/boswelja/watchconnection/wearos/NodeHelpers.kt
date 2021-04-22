package com.boswelja.watchconnection.wearos

import com.google.android.gms.wearable.Node

object NodeHelpers {
    class DummyNode(
        private val id: String,
        private val name: String,
        private val isNearby: Boolean
    ) : Node {
        override fun getId(): String = id
        override fun getDisplayName(): String = name
        override fun isNearby(): Boolean = isNearby
    }

    fun createDummyNodes(count: Int): List<Node> =
        (1..(count + 1)).map { num ->
            DummyNode("id-$num", "Node $num", true)
        }
}
