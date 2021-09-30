package com.watchconnection.sample.message

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.common.message.Message
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.boswelja.watchconnection.core.discovery.DiscoveryClient
import com.boswelja.watchconnection.core.message.MessageClient
import com.boswelja.watchconnection.serializers.StringSerializer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val messageClient: MessageClient,
    private val discoveryClient: DiscoveryClient
) : ViewModel() {
    var selectedWatch by mutableStateOf<Watch?>(null)
    val availableWatches = mutableStateListOf<Watch>()
    val incomingMessages = mutableStateListOf<ReceivedMessage<String>>()
    val sentMessages = mutableStateListOf<Message<String>>()

    init {
        viewModelScope.launch {
            messageClient.incomingMessages(StringSerializer(setOf("message-path"))).collect {
                incomingMessages.add(0, it)
            }
        }
        viewModelScope.launch {
            discoveryClient.allWatches().collect {
                availableWatches.clear()
                availableWatches.addAll(it)
                if (selectedWatch == null) selectedWatch = it.firstOrNull()
            }
        }
    }

    fun sendMessageTo(watch: Watch, text: String) {
        viewModelScope.launch {
            val message = Message(
                "message-path",
                text
            )
            messageClient.sendMessage(
                watch,
                message
            )
            sentMessages.add(message)
        }
    }
}