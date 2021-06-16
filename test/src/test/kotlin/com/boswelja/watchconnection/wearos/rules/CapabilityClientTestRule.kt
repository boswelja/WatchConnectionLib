package com.boswelja.watchconnection.wearos.rules

import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Node
import io.mockk.every
import io.mockk.mockk
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class CapabilityClientTestRule(
    private val initialNodes: List<Node> = emptyList()
) : TestRule {

    private val listenerMap = HashMap<String, CapabilityClient.OnCapabilityChangedListener>()

    lateinit var capabilityClient: CapabilityClient

    override fun apply(base: Statement?, description: Description?): Statement {
        return CapabilityClientTestRuleStatement(base)
    }

    fun changeCapabilityInfo(name: String, nodes: List<Node>) {
        val info = DummyCapabilityInfo(name, nodes)
        every { capabilityClient.getCapability(name, any()) } returns Tasks.forResult(info)
        listenerMap[name]?.onCapabilityChanged(info)
    }

    inner class CapabilityClientTestRuleStatement(private val base: Statement?) : Statement() {
        override fun evaluate() {
            // Set up CapabilityClient mock
            capabilityClient = mockk(relaxed = true)

            every { capabilityClient.getCapability(any(), any()) } answers {
                Tasks.forResult(DummyCapabilityInfo(firstArg(), initialNodes))
            }
            every { capabilityClient.addListener(any(), any()) } answers {
                listenerMap[secondArg()] = firstArg()
                Tasks.forResult(null)
            }

            try {
                base?.evaluate()
            } finally { }
        }
    }
}
