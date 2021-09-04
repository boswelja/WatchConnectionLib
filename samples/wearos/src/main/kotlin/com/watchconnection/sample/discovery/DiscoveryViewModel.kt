package com.watchconnection.sample.discovery

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.boswelja.watchconnection.core.Phone
import com.watchconnection.sample.discoveryClient
import kotlinx.coroutines.launch

class DiscoveryViewModel(application: Application) : AndroidViewModel(application) {
    private val discoveryClient = application.discoveryClient()

    var pairedPhone by mutableStateOf<Phone?>(null)
    val status = discoveryClient.phoneStatus()

    init {
        viewModelScope.launch {
            pairedPhone = discoveryClient.pairedPhone()
        }
    }
}
