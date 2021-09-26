package com.watchconnection.sample.discovery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults.primaryChipColors
import androidx.wear.compose.material.ChipDefaults.secondaryChipColors
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListScope
import androidx.wear.compose.material.Text
import com.boswelja.watchconnection.common.Phone
import com.watchconnection.sample.R
import kotlinx.coroutines.Dispatchers

@Composable
fun DiscoveryScreen(
    modifier: Modifier = Modifier
) {
    val viewModel = hiltViewModel<DiscoveryViewModel>()
    val phone = viewModel.pairedPhone
    val capabilities by viewModel.phoneCapabilities.collectAsState(emptyList(), Dispatchers.IO)

    ScalingLazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        pairedPhoneInfo(phone)
        phoneCapabilities(capabilities.toList())
        localCapabilities(
            capabilityValues = viewModel.localCapabilities,
            onAddCapability = viewModel::addLocalCapability,
            onRemoveCapability = viewModel::removeLocalCapability
        )
    }
}

fun ScalingLazyListScope.pairedPhoneInfo(phone: Phone?) {
    item {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.paired_phone),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.caption1
        )
    }
    item {
        Chip(
            label = { Text(phone?.name ?: stringResource(id = R.string.loading)) },
            onClick = { }
        )
    }
}

fun ScalingLazyListScope.phoneCapabilities(capabilities: List<String>) {
    item {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.phone_capabilities),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.caption1
        )
    }
    if (capabilities.isNotEmpty()) {
        items(capabilities.count()) { index ->
            val capability = capabilities[index]
            Chip(
                label = { Text(capability) },
                onClick = { },
                colors = secondaryChipColors()
            )
        }
    } else {
        item {
            Chip(
                label = { Text(stringResource(R.string.no_capabilities)) },
                onClick = { },
                colors = secondaryChipColors()
            )
        }
    }
}

fun ScalingLazyListScope.localCapabilities(
    capabilityValues: Map<Capability, Boolean>,
    onAddCapability: (Capability) -> Unit,
    onRemoveCapability: (Capability) -> Unit
) {
    item {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.local_capabilities),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.caption1
        )
    }
    items(Capability.values().size) { index ->
        val capability = Capability.values()[index]
        val enabled = capabilityValues[capability] ?: false

        Chip(
            label = { Text(capability.name) },
            secondaryLabel = {
                if (enabled) {
                    Text(stringResource(R.string.capability_remove))
                } else {
                    Text(stringResource(R.string.capability_add))
                }
            },
            onClick = {
                if (enabled) {
                    onRemoveCapability(capability)
                } else {
                    onAddCapability(capability)
                }
            },
            colors = if (enabled) primaryChipColors() else secondaryChipColors()
        )
    }
}
