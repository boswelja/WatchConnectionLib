package com.watchconnection.sample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                NavHost(
                    navController = navController,
                    startDestination = Destinations.Main.route
                ) {
                    composable(Destinations.Main.route) {
                        MainScreen(
                            onNavigateTo = { navController.navigate(it.route) }
                        )
                    }
                }
            }
        }
    }
}

enum class Destinations(val route: String) {
    Main("main")
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onNavigateTo: (Destinations) -> Unit
) {
    Text("Hello, World!")
}