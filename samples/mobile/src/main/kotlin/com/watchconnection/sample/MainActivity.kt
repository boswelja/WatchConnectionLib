package com.watchconnection.sample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.darkColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.watchconnection.sample.discovery.DiscoveryScreen
import com.watchconnection.sample.message.MessageScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme(
                colors = if (isSystemInDarkTheme()) darkColors() else lightColors()
            ) {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val showUpButton by remember(backStackEntry) {
                    mutableStateOf(backStackEntry?.destination?.route != Destinations.Main.route)
                }

                Scaffold(
                    topBar = {
                        if (showUpButton) {
                            TopAppBar(
                                navigationIcon = {
                                    IconButton(
                                        onClick = { navController.popBackStack() }
                                    ) {
                                        Icon(Icons.Default.ArrowBack, null)
                                    }
                                },
                                actions = { },
                                title = {
                                    Text(stringResource(R.string.app_name))
                                }
                            )
                        } else {
                            TopAppBar(
                                actions = { },
                                title = {
                                    Text(stringResource(R.string.app_name))
                                }
                            )
                        }
                    }
                ) {
                    val screenModifier = Modifier.fillMaxSize()

                    NavHost(
                        navController = navController,
                        startDestination = Destinations.Main.route
                    ) {
                        composable(Destinations.Main.route) {
                            MainScreen(
                                modifier = screenModifier,
                                onNavigateTo = { navController.navigate(it.route) }
                            )
                        }
                        composable(Destinations.Messages.route) {
                            MessageScreen(screenModifier)
                        }
                        composable(Destinations.Discovery.route) {
                            DiscoveryScreen(screenModifier)
                        }
                    }
                }
            }
        }
    }
}

enum class Destinations(val route: String) {
    Main("main"),
    Messages("messages"),
    Discovery("discovery")
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onNavigateTo: (Destinations) -> Unit
) {
    val viewModel: MainViewModel = hiltViewModel()
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            MainItem(
                label = stringResource(R.string.message_screen_title),
                onClick = { onNavigateTo(Destinations.Messages) }
            )
        }
        item {
            MainItem(
                label = stringResource(R.string.discovery_screen_title),
                onClick = { onNavigateTo(Destinations.Discovery) }
            )
        }
        item {
            AvailablePlatformsCard(
                isWearOSAvailable = viewModel.isWearOSAvailable
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainItem(
    modifier: Modifier = Modifier,
    label: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = label,
                style = MaterialTheme.typography.h5
            )
            Icon(
                Icons.Default.NavigateNext,
                null
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AvailablePlatformsCard(
    modifier: Modifier = Modifier,
    contentPadding: Dp = 16.dp,
    isWearOSAvailable: Boolean
) {
    Card(modifier) {
        Column(Modifier.padding(contentPadding)) {
            Text(
                text = stringResource(R.string.available_platforms),
                style = MaterialTheme.typography.h4
            )
            Spacer(Modifier.height(contentPadding))
            ListItem(
                icon = {
                    val icon = if (isWearOSAvailable) {
                        Icons.Default.CheckCircle
                    } else {
                        Icons.Default.Cancel
                    }
                    Icon(icon, null)
                },
                text = {
                    val platformName = stringResource(R.string.platform_wearos)
                    val text = if (isWearOSAvailable) {
                        stringResource(R.string.platform_available, platformName)
                    } else {
                        stringResource(R.string.platform_unavailable, platformName)
                    }
                    Text(text)
                }
            )
        }
    }
}
