package com.mind.play.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mind.play.core.components.MindPlayBottomNavigation
import com.mind.play.data.repository.SettingsRepository
import com.mind.play.ui.dashboard.HomeScreen
import com.mind.play.ui.games.GamesScreen
import com.mind.play.ui.onboarding.OnboardingScreen
import com.mind.play.ui.onboarding.WelcomeScreen
import com.mind.play.ui.settings.SettingsScreen
import com.mind.play.ui.splash.SplashScreen
import org.koin.compose.koinInject

@Composable
fun MindPlayNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val settingsRepository: SettingsRepository = koinInject()
    val appSettings by settingsRepository.settings.collectAsState()
    val settingsLoaded = appSettings != null
    val shouldShowOnboarding = appSettings?.let { !it.onboardingCompleted } ?: false
    
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Games.route,
        Screen.Settings.route
    )
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                MindPlayBottomNavigation(
                    currentRoute = currentRoute ?: Screen.Home.route,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(paddingValues),
            enterTransition = { NavigationAnimations.fadeInTransition() },
            exitTransition = { NavigationAnimations.fadeOutTransition() }
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    settingsLoaded = settingsLoaded,
                    shouldShowOnboarding = shouldShowOnboarding,
                    onNavigateToWelcome = {
                        navController.navigate(Screen.Welcome.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(Screen.Welcome.route) {
                WelcomeScreen(
                    onStartClick = {
                        navController.navigate(Screen.Onboarding.route)
                    }
                )
            }

            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onFinished = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(Screen.Home.route) {
                HomeScreen()
            }
            
            composable(Screen.Games.route) {
                GamesScreen()
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
