package com.watchconnection.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.SwipeDismissTarget
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberSwipeToDismissBoxState
import com.watchconnection.sample.discovery.DiscoveryScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainScreen(Modifier.fillMaxSize())
            }
        }
    }
}

@OptIn(ExperimentalWearMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    // This doesn't scale very well, and should probably be replaced with compose-navigation once
    // support for SwipeToDismissBox arrives
    var discoveryScreenVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var messageScreenVisible by rememberSaveable {
        mutableStateOf(false)
    }
    ScalingLazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 64.dp, horizontal = 16.dp)
    ) {
        item {
            Chip(
                onClick = { discoveryScreenVisible = true },
                label = { Text(stringResource(R.string.discovery_screen_title)) },
                secondaryLabel = { Text(stringResource(R.string.discovery_screen_summary)) }
            )
        }
        item {
            Chip(
                onClick = { messageScreenVisible = true },
                label = { Text(stringResource(R.string.message_screen_title)) },
                secondaryLabel = { Text(stringResource(R.string.message_screen_summary)) }
            )
        }
    }
    AnimatedVisibility(
        visible = discoveryScreenVisible,
        enter = fadeIn(),
        exit = ExitTransition.None
    ) {
        val state = rememberSwipeToDismissBoxState()
        LaunchedEffect(state.currentValue) {
            if (state.currentValue == SwipeDismissTarget.Dismissal) {
                discoveryScreenVisible = false
            }
        }
        SwipeToDismissBox(state = state) {
            DiscoveryScreen(
                Modifier
                    .background(MaterialTheme.colors.background)
                    .then(modifier)
            )
        }
    }
//    AnimatedVisibility(
//        visible = messageScreenVisible,
//        enter = fadeIn(),
//        exit = ExitTransition.None
//    ) {
//        val state = rememberSwipeToDismissBoxState()
//        LaunchedEffect(state.currentValue) {
//            if (state.currentValue == SwipeDismissTarget.Dismissal) {
//                discoveryScreenVisible = false
//            }
//        }
//        SwipeToDismissBox(state = state) {
//            // TODO
//        }
//    }
}
