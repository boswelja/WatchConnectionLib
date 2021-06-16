package com.boswelja.watchconnection.wearos.rules

import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.NodeClient
import io.mockk.every
import io.mockk.mockk
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class NodeClientTestRule(
    private val initialNodes: List<Node> = emptyList()
) : TestRule {

    lateinit var nodeClient: NodeClient

    override fun apply(base: Statement?, description: Description?): Statement {
        return NodeClientTestRuleStatement(base)
    }

    fun setConnectedNodes(nodes: List<Node>) {
        every { nodeClient.connectedNodes } answers {
            Tasks.forResult(nodes)
        }
    }

    inner class NodeClientTestRuleStatement(private val base: Statement?) : Statement() {
        override fun evaluate() {
            // Set up CapabilityClient mock
            nodeClient = mockk(relaxed = true)

            every { nodeClient.connectedNodes } answers {
                Tasks.forResult(initialNodes)
            }

            try {
                base?.evaluate()
            } finally { }
        }
    }
}
