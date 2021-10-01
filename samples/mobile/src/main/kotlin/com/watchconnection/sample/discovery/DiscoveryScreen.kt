package com.watchconnection.sample.discovery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Checkbox
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.watchconnection.sample.R
import com.watchconnection.sample.common.SectionCard

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DiscoveryScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: DiscoveryViewModel = hiltViewModel()

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            LocalCapabilities(
                capabilityValues = viewModel.localCapabilities,
                onCapabilityAdded = viewModel::addLocalCapability,
                onCapabilityRemoved = viewModel::removeLocalCapability
            )
        }
        items(viewModel.availableWatches) { watch ->
            SectionCard(
                title = stringResource(R.string.watch_capabilities, watch.name)
            ) {
                val capabilities = viewModel.watchCapabilities[watch]
                if (capabilities.isNullOrEmpty()) {
                    ListItem(text = { Text(stringResource(R.string.no_capabilities)) })
                } else {
                    capabilities.forEach { capability ->
                        ListItem(text = { Text(capability) })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LocalCapabilities(
    modifier: Modifier = Modifier,
    capabilityValues: Map<Capability, Boolean>,
    onCapabilityAdded: (Capability) -> Unit,
    onCapabilityRemoved: (Capability) -> Unit
) {
    SectionCard(
        modifier = modifier,
        title = stringResource(R.string.local_capabilities)
    ) {
        capabilityValues.forEach { (capability, enabled) ->
            ListItem(
                text = { Text(capability.name) },
                trailing = { Checkbox(checked = enabled, onCheckedChange = null) },
                modifier = Modifier.clickable {
                    if (enabled) {
                        onCapabilityRemoved(capability)
                    } else {
                        onCapabilityAdded(capability)
                    }
                }
            )
        }
    }
}
