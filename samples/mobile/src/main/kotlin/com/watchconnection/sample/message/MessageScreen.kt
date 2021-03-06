package com.watchconnection.sample.message

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.boswelja.watchconnection.common.Watch
import com.boswelja.watchconnection.common.message.ReceivedMessage
import com.watchconnection.sample.R
import com.watchconnection.sample.common.SectionCard

@Composable
fun MessageScreen(modifier: Modifier = Modifier) {
    val viewModel: MessageViewModel = hiltViewModel()
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        sendMessage(
            availableWatches = viewModel.availableWatches,
            selectedWatch = viewModel.selectedWatch,
            onWatchSelected = { viewModel.selectedWatch = it },
            onSendMessage = { text ->
                viewModel.selectedWatch?.let {
                    viewModel.sendMessageTo(it, text)
                }
            }
        )
        receivedMessages(viewModel.incomingMessages)
    }
}

/**
 * Add message sending components to a [LazyListScope].
 */
fun LazyListScope.sendMessage(
    availableWatches: List<Watch>,
    selectedWatch: Watch?,
    onWatchSelected: (Watch) -> Unit,
    onSendMessage: (String) -> Unit
) {
    item {
        SectionCard(title = stringResource(R.string.send_message)) {
            Row {
                var dropdownVisible by remember { mutableStateOf(false) }

                Text(stringResource(R.string.target))
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier.clickable { dropdownVisible = true }
                ) {
                    Row {
                        Text(selectedWatch?.name ?: stringResource(R.string.no_watches))
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                    DropdownMenu(
                        expanded = dropdownVisible,
                        onDismissRequest = { dropdownVisible = false }
                    ) {
                        availableWatches.forEach { watch ->
                            DropdownMenuItem(
                                onClick = {
                                    onWatchSelected(watch)
                                    dropdownVisible = false
                                }
                            ) {
                                Text(watch.name)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                var text by remember { mutableStateOf("") }

                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = text,
                    onValueChange = { text = it },
                    singleLine = true,
                    placeholder = { Text("Message") },
                    trailingIcon = {
                        IconButton(onClick = { text = "" }) {
                            Icon(Icons.Default.Clear, null)
                        }
                    }
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        onSendMessage(text)
                        text = ""
                    }
                ) {
                    Icon(Icons.Default.Send, null)
                }
            }
        }
    }
}

/**
 * Add received message components to [LazyListScope]
 */
@OptIn(ExperimentalMaterialApi::class)
fun LazyListScope.receivedMessages(
    receivedMessages: List<ReceivedMessage<String>>
) {
    item {
        SectionCard(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.received_messages)
        ) {
            receivedMessages.forEach { receivedMessage ->
                ListItem(
                    text = { Text(receivedMessage.path) },
                    secondaryText = { Text(receivedMessage.data) },
                    overlineText = { Text(receivedMessage.sourceUid) }
                )
            }
        }
    }
}
