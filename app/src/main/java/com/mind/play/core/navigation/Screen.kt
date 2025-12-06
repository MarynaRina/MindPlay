package com.mind.play.core.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Welcome : Screen("welcome")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Games : Screen("games")
    object Settings : Screen("settings")
    object GameArytmetyka : Screen("game/arytmetyka")
    object GameMemory : Screen("game/memory")
    object GamePuzzle : Screen("game/puzzle")
    object GameSimon : Screen("game/simon")
    object GameUwaga : Screen("game/uwaga")
    object GamePary : Screen("game/pary")
}

sealed class BottomNavItem(
    val route: String,
    val label: String
) {
    object Home : BottomNavItem(Screen.Home.route, "Główna")
    object Games : BottomNavItem(Screen.Games.route, "Gry")
    object Settings : BottomNavItem(Screen.Settings.route, "Ustawienia")
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Games,
    BottomNavItem.Settings
)
