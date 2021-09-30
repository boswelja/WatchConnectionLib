package com.watchconnection.sample.message

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.boswelja.watchconnection.common.Watch

@Composable
fun MessageScreen(modifier: Modifier = Modifier) {
    val viewModel: MessageViewModel = hiltViewModel()
    LazyColumn {
        sendMessage(viewModel.availableWatches, viewModel::sendMessageTo)
    }
}

fun LazyListScope.sendMessage(
    availableWatches: List<Watch>,
    onSendMessage: (Watch, String) -> Unit
) {
    item {
        var text by remember { mutableStateOf("") }
        Column {
            Row {
                TextField(
                    modifier = Modifier.weight(1f),
                    value = text,
                    onValueChange = { text = it }
                )
                IconButton(
                    onClick = { }
                ) {
                    Icon(Icons.Default.Send, null)
                }
            }
        }
    }
}
