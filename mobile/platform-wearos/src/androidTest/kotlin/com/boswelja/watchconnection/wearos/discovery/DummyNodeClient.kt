package com.boswelja.watchconnection.wearos.discovery

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.NodeClient

internal class DummyNodeClient(
    context: Context
) : NodeClient(context, Settings.DEFAULT_SETTINGS) {

    val connectedNodes = mutableListOf<Node>()

    override fun getLocalNode(): Task<Node> {
        val localNode = DummyNode(
            "id",
            "Local Node",
            true
        )
        return Tasks.forResult(localNode)
    }

    override fun getConnectedNodes(): Task<MutableList<Node>> {
        return Tasks.forResult(connectedNodes)
    }

    override fun getCompanionPackageForNode(nodeId: String): Task<String> {
        TODO("Not yet implemented")
    }
}

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
