package com.boswelja.watchconnection.wearos.discovery

import android.content.Context
import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.Node

internal class DummyCapabilityClient(
    context: Context
) : CapabilityClient(context, Settings.DEFAULT_SETTINGS) {

    val nodesWithCapability = mutableMapOf<String, MutableSet<Node>>()

    val localCapabilities = mutableListOf<String>()
    val listeners = mutableMapOf<String, OnCapabilityChangedListener>()

    override fun getCapability(capability: String, p1: Int): Task<CapabilityInfo> {
        return Tasks.forResult(
            DummyCapabilityInfo(capability, nodesWithCapability[capability] ?: mutableSetOf())
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun getAllCapabilities(p0: Int): Task<MutableMap<String, CapabilityInfo>> {
        val capabilities = nodesWithCapability
            .mapValues { (key, value) -> DummyCapabilityInfo(key, value) }
            .toMutableMap() as MutableMap<String, CapabilityInfo>
        return Tasks.forResult(capabilities)
    }

    override fun addLocalCapability(capability: String): Task<Void> {
        localCapabilities.add(capability)
        return Tasks.forResult(null)
    }

    override fun removeLocalCapability(capability: String): Task<Void> {
        localCapabilities.remove(capability)
        return Tasks.forResult(null)
    }

    override fun addListener(
        listener: OnCapabilityChangedListener,
        capability: String
    ): Task<Void> {
        listeners[capability] = listener
        return Tasks.forResult(null)
    }

    override fun addListener(
        listener: OnCapabilityChangedListener,
        p1: Uri,
        p2: Int
    ): Task<Void> {
        TODO("Not yet implemented")
    }

    override fun removeListener(
        listener: OnCapabilityChangedListener,
        capability: String
    ): Task<Boolean> {
        val removed = listeners.remove(capability, listener)
        return Tasks.forResult(removed)
    }

    override fun removeListener(listener: OnCapabilityChangedListener): Task<Boolean> {
        val key = listeners
            .firstNotNullOfOrNull { (key, value) -> if (value == listener) key else null }
        val removed = key?.let { listeners.remove(key, listener) } ?: false
        return Tasks.forResult(removed)
    }
}

internal class DummyCapabilityInfo(
    private val name: String,
    private val nodes: MutableSet<Node>
) : CapabilityInfo {
    override fun getName(): String = name
    override fun getNodes(): MutableSet<Node> = nodes
}
