package com.watchconnection.sample.message

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.boswelja.watchconnection.common.message.serialized.StringSerializer
import com.boswelja.watchconnection.common.message.serialized.TypedMessage
import com.watchconnection.sample.messageClient
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MessageViewModel(application: Application) : AndroidViewModel(application) {
    private val messageClient = application.messageClient()
    val incomingMessages = mutableStateListOf<ReceivedMessage<String>>()

    init {
        viewModelScope.launch {
            messageClient.incomingMessages(StringSerializer(setOf("message-path"))).collect {
                incomingMessages.add(0, it)
            }
        }
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            messageClient.sendMessage(
                TypedMessage(
                    "message-path",
                    text
                )
            )
        }
    }
}
