package com.watchconnection.sample.message

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SendToMobile
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.LocalTextStyle
import androidx.wear.compose.material.Text
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.watchconnection.sample.R

@Composable
fun MessageScreen(
    modifier: Modifier = Modifier
) {
    val viewModel = hiltViewModel<MessageViewModel>()
    val receivedMessages = viewModel.incomingMessages
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 64.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            SendMessage(onSendText = viewModel::sendMessage)
        }
        item {
            ReceivedMessages(receivedMessages = receivedMessages)
        }
    }
}

@Composable
fun SendMessage(
    modifier: Modifier = Modifier,
    onSendText: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var text by rememberSaveable {
        mutableStateOf("")
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.send_message))
        Card(onClick = { }) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    textStyle = LocalTextStyle.current,
                    singleLine = true,
                    keyboardActions = KeyboardActions { focusManager.clearFocus() }
                )
                CompactButton(onClick = { onSendText(text) }) {
                    Icon(Icons.Default.SendToMobile, stringResource(R.string.send_message))
                }
            }
        }
    }
}

@Composable
fun ReceivedMessages(
    modifier: Modifier = Modifier,
    receivedMessages: List<ReceivedMessage<String>>
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.received_messages))
        Card(onClick = { }) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (receivedMessages.isEmpty()) {
                    Text(stringResource(R.string.no_received_messages))
                } else {
                    receivedMessages.forEach {
                        Text(it.data)
                    }
                }
            }
        }
    }
}
