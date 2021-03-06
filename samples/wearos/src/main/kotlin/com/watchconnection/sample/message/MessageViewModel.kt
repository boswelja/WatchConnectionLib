package com.watchconnection.sample.message

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boswelja.watchconnection.common.message.Message
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.boswelja.watchconnection.serialization.MessageHandler
import com.boswelja.watchconnection.serialization.StringSerializer
import com.boswelja.watchconnection.wear.discovery.DiscoveryClient
import com.boswelja.watchconnection.wear.message.MessageClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val discoveryClient: DiscoveryClient,
    private val messageClient: MessageClient
) : ViewModel() {
    private val messageHandler =
        MessageHandler(StringSerializer(setOf("message-path")), messageClient)

    val incomingMessages = mutableStateListOf<ReceivedMessage<String>>()
    val sentMessages = mutableStateListOf<Message<String>>()

    init {
        viewModelScope.launch {
            messageHandler.incomingMessages().collectLatest {
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
                messageHandler.sendMessage(
                    pairedPhone.uid,
                    message
                )
                sentMessages.add(message)
            }
        }
    }
}
