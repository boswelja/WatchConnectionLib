package com.watchconnection.sample.message

import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.ui.platform.LocalContext
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
import com.boswelja.watchconnection.common.message.Message
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.watchconnection.sample.R
import com.watchconnection.sample.contracts.RecognizeSpeech
import com.watchconnection.sample.contracts.RemoteInput

@Composable
fun MessageScreen(
    modifier: Modifier = Modifier
) {
    val viewModel = hiltViewModel<MessageViewModel>()
    val sentMessages = viewModel.sentMessages
    val receivedMessages = viewModel.incomingMessages

    ScalingLazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 32.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        sendMessage(viewModel::sendMessage)
        sentMessages(sentMessages)
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
        val context = LocalContext.current
        val voiceInputLauncher = rememberLauncherForActivityResult(RecognizeSpeech()) { text ->
            text?.let(onSendText)
        }
        val remoteInputLauncher = rememberLauncherForActivityResult(RemoteInput()) { text ->
            text?.let(onSendText)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            Button(
                onClick = {
                    voiceInputLauncher.launch(RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                }
            ) {
                Icon(Icons.Default.KeyboardVoice, "Voice Input")
            }
            Button(
                onClick = {
                    remoteInputLauncher.launch(context.getString(R.string.send_message))
                }
            ) {
                Icon(Icons.Default.Keyboard, "Text Input")
            }
        }
    }
}

fun ScalingLazyListScope.sentMessages(
        sentMessages: List<Message<String>>
) {
    item {
        ListHeader {
            Text(stringResource(R.string.sent_messages))
        }
    }
    if (sentMessages.isNotEmpty()) {
        items(sentMessages.count()) { index ->
            val message = sentMessages[index]
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
