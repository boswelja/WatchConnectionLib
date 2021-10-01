package com.watchconnection.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.watchconnection.sample.discovery.DiscoveryScreen
import com.watchconnection.sample.message.MessageScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalWearMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val screenModifier = Modifier.fillMaxSize()
                val navController = rememberSwipeDismissableNavController()

                Scaffold(
                    timeText = { TimeText() }
                ) {
                    SwipeDismissableNavHost(
                        navController = navController,
                        startDestination = Destination.Main.route
                    ) {
                        composable(Destination.Main.route) {
                            MainScreen(
                                modifier = screenModifier,
                                onNavigate = { destination ->
                                    navController.navigate(destination.route) {
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                        composable(Destination.Discovery.route) {
                            DiscoveryScreen(screenModifier)
                        }
                        composable(Destination.Messages.route) {
                            MessageScreen(screenModifier)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onNavigate: (Destination) -> Unit
) {
    ScalingLazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 64.dp, horizontal = 16.dp)
    ) {
        item {
            Chip(
                onClick = { onNavigate(Destination.Discovery) },
                label = { Text(stringResource(R.string.discovery_screen_title)) }
            )
        }
        item {
            Chip(
                onClick = { onNavigate(Destination.Messages) },
                label = { Text(stringResource(R.string.message_screen_title)) }
            )
        }
    }
}

enum class Destination(val route: String) {
    Main("main"),
    Discovery("discovery"),
    Messages("messages")
}
