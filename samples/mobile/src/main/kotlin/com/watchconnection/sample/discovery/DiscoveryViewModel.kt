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

    init {
        viewModelScope.launch {
            discoveryClient.allWatches().collect {
                availableWatches.clear()
                availableWatches.addAll(it)
            }
        }
    }
}
