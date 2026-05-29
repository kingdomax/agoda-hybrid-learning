package com.example.agodahybridlearning.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agodahybridlearning.data.SettingsRepository
import com.example.agodahybridlearning.ui.HomeScreen
import com.example.agodahybridlearning.ui.SettingsScreen
import com.example.agodahybridlearning.ui.WebShellScreen

@Composable
fun AppNavigation(settingsRepository: SettingsRepository) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onOpenSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onOpenWebShell = {
                    navController.navigate(Screen.WebShell.route)
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                settingsRepository = settingsRepository,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.WebShell.route) {
            WebShellScreen(
                url = "http://10.0.2.2:5173", // 10.0.2.2 = your Windows host machine
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}