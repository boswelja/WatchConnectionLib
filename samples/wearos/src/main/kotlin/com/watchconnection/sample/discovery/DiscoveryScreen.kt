package com.watchconnection.sample.discovery

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Text
import com.boswelja.watchconnection.common.discovery.Status
import com.boswelja.watchconnection.core.Phone
import com.watchconnection.sample.R
import kotlinx.coroutines.Dispatchers

@Composable
fun DiscoveryScreen(
    modifier: Modifier = Modifier
) {
    val viewModel = viewModel<DiscoveryViewModel>()
    val phone = viewModel.pairedPhone
    val status by viewModel.status.collectAsState(Status.CONNECTING, Dispatchers.IO)
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 64.dp, horizontal = 16.dp)
    ) {
        item {
            PairedPhoneInfo(
                modifier = Modifier.fillMaxWidth(),
                phone = phone,
                status = status
            )
        }
    }
}

@Composable
fun PairedPhoneInfo(
    modifier: Modifier = Modifier,
    phone: Phone?,
    status: Status
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(id = R.string.paired_phone))
        Chip(
            label = { Text(phone?.name ?: stringResource(id = R.string.loading)) },
            secondaryLabel = { Text(status.name) },
            onClick = { }
        )
    }
}
