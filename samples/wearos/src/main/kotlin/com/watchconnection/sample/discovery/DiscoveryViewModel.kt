package com.watchconnection.sample.discovery

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boswelja.watchconnection.common.Phone
import com.boswelja.watchconnection.wear.discovery.DiscoveryClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoveryViewModel @Inject constructor(
    private val discoveryClient: DiscoveryClient
) : ViewModel() {

    var pairedPhone by mutableStateOf<Phone?>(null)
    val phoneCapabilities = discoveryClient.phoneCapabilities()

    init {
        viewModelScope.launch {
            pairedPhone = discoveryClient.pairedPhone()
        }
    }
}
