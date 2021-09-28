package com.watchconnection.sample.message

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults.secondaryChipColors
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListScope
import androidx.wear.compose.material.Text
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.watchconnection.sample.R

@Composable
fun MessageScreen(
    modifier: Modifier = Modifier
) {
    val viewModel = hiltViewModel<MessageViewModel>()
    val receivedMessages = viewModel.incomingMessages

    ScalingLazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 64.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        sendMessage(viewModel::sendMessage)
        receivedMessages(receivedMessages)
    }
}

fun ScalingLazyListScope.sendMessage(
    onSendText: (String) -> Unit
) {
    item {
        ListHeader {
            Text(stringResource(id = R.string.send_message))
        }
    }
    item {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            Button(
                onClick = { }
            ) {
                Icon(Icons.Default.KeyboardVoice, "Voice Input")
            }
            Button(
                onClick = { }
            ) {
                Icon(Icons.Default.Keyboard, "Text Input")
            }
        }
    }
}

fun ScalingLazyListScope.receivedMessages(
    receivedMessages: List<ReceivedMessage<String>>
) {
    item {
        ListHeader {
            Text(stringResource(R.string.received_messages))
        }
    }
    if (receivedMessages.isNotEmpty()) {
        items(receivedMessages.count()) { index ->
            val message = receivedMessages[index]
            Chip(
                label = { Text(message.path) },
                secondaryLabel = { Text(message.data) },
                onClick = { },
                colors = secondaryChipColors()
            )
        }
    } else {
        item {
            Chip(
                label = { Text(stringResource(R.string.no_received_messages)) },
                onClick = { },
                colors = secondaryChipColors()
            )
        }
    }
}
