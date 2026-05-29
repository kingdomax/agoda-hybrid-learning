package com.example.agodahybridlearning.navigation

sealed class Screen(val route: String, val title: String) {
    data object Home : Screen("home", "Home")
    data object Settings : Screen("settings", "Settings")
    data object WebShell : Screen("web-shell", "Web Shell")
}