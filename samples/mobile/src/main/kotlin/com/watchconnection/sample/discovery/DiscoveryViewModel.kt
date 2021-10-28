package com.watchconnection.sample.discovery

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.core.discovery.DiscoveryClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoveryViewModel @Inject constructor(
    private val discoveryClient: DiscoveryClient
) : ViewModel() {
    val availableWatches = mutableStateListOf<Watch>()
    val localCapabilities = mutableStateMapOf(
        *Capability.values().map { Pair(it, false) }.toTypedArray()
    )
    val watchCapabilities = mutableStateMapOf<Watch, Set<String>>()

    init {
        viewModelScope.launch {
            discoveryClient.allWatches().collect {
                availableWatches.clear()
                availableWatches.addAll(it)
                it.forEach { watch ->
                    refreshCapabilitiesFor(watch)
                }
            }
        }
    }

    fun addLocalCapability(capability: Capability) {
        viewModelScope.launch {
            discoveryClient.addLocalCapability(capability.name)
            localCapabilities[capability] = true
        }
    }

    fun removeLocalCapability(capability: Capability) {
        viewModelScope.launch {
            discoveryClient.removeLocalCapability(capability.name)
            localCapabilities[capability] = false
        }
    }

    fun refreshCapabilitiesFor(watch: Watch) {
        viewModelScope.launch {
            val newCapabilities = discoveryClient.getCapabilitiesFor(watch)
            watchCapabilities[watch] = newCapabilities
        }
    }
}
