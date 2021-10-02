package com.watchconnection.sample.message

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boswelja.watchconnection.common.message.Message
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.boswelja.watchconnection.serializers.StringSerializer
import com.boswelja.watchconnection.wear.discovery.DiscoveryClient
import com.boswelja.watchconnection.wear.message.MessageClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val discoveryClient: DiscoveryClient,
    private val messageClient: MessageClient
) : ViewModel() {
    val incomingMessages = mutableStateListOf<ReceivedMessage<String>>()
    val sentMessages = mutableStateListOf<Message<String>>()

    init {
        viewModelScope.launch {
            messageClient.incomingMessages(StringSerializer(setOf("message-path"))).collect {
                incomingMessages.add(0, it)
            }
        }
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            val pairedPhone = discoveryClient.pairedPhone()
            if (pairedPhone != null) {
                val message = Message(
                    "message-path",
                    text
                )
                messageClient.sendMessage(
                    pairedPhone,
                    message
                )
                sentMessages.add(message)
            }
        }
    }
}
