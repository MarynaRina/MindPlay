package com.mind.play.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mind.play.core.components.MindPlayBottomNavigation
import com.mind.play.core.sound.SoundManager
import com.mind.play.data.repository.SettingsRepository
import com.mind.play.ui.dashboard.HomeScreen
import com.mind.play.ui.games.GamesScreen
import com.mind.play.ui.games.arithmetic.ArithmeticGameScreen
import com.mind.play.ui.games.memory.MemoryScreen
import com.mind.play.ui.games.pary.ParyScreen
import com.mind.play.ui.games.puzzle.PuzzleGameScreen
import com.mind.play.ui.games.simon.SimonGameScreen
import com.mind.play.ui.games.uwaga.UwagaGameScreen
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
    val soundManager: SoundManager = koinInject()
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
            composable(
                route = Screen.Splash.route,
                enterTransition = { NavigationAnimations.fadeInTransition() },
                exitTransition = { NavigationAnimations.fadeOutTransition() }
            ) {
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
            
            composable(
                route = Screen.Welcome.route,
                enterTransition = { NavigationAnimations.fadeInTransition() },
                exitTransition = { NavigationAnimations.fadeOutTransition() }
            ) {
                WelcomeScreen(
                    onStartClick = {
                        navController.navigate(Screen.Onboarding.route)
                    }
                )
            }

            composable(
                route = Screen.Onboarding.route,
                enterTransition = { NavigationAnimations.fadeInTransition() },
                exitTransition = { NavigationAnimations.fadeOutTransition() }
            ) {
                OnboardingScreen(
                    onFinished = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(
                route = Screen.Home.route,
                enterTransition = { NavigationAnimations.fadeInTransition() },
                exitTransition = { NavigationAnimations.fadeOutTransition() },
                popEnterTransition = { NavigationAnimations.fadeInTransition() },
                popExitTransition = { NavigationAnimations.fadeOutTransition() }
            ) {
                LaunchedEffect(Unit) {
                    soundManager.startBackgroundMusic()
                }
                HomeScreen()
            }
            
            composable(
                route = Screen.Games.route,
                enterTransition = { NavigationAnimations.fadeInTransition() },
                exitTransition = { NavigationAnimations.fadeOutTransition() },
                popEnterTransition = { NavigationAnimations.fadeInTransition() },
                popExitTransition = { NavigationAnimations.fadeOutTransition() }
            ) {
                GamesScreen(
                    onNavigateToGame = { gameId ->
                        when (gameId) {
                            "arytmetyka" -> navController.navigate(Screen.GameArytmetyka.route)
                            "memory" -> navController.navigate(Screen.GameMemory.route)
                            "pary" -> navController.navigate(Screen.GamePary.route)
                            "puzzle" -> navController.navigate(Screen.GamePuzzle.route)
                            "uwaga" -> navController.navigate(Screen.GameUwaga.route)
                            "simon" -> navController.navigate(Screen.GameSimon.route)
                        }
                    }
                )
            }
            
            composable(
                route = Screen.GameArytmetyka.route,
                enterTransition = { NavigationAnimations.fadeInTransition() },
                exitTransition = { NavigationAnimations.fadeOutTransition() },
                popEnterTransition = { NavigationAnimations.fadeInTransition() },
                popExitTransition = { NavigationAnimations.fadeOutTransition() }
            ) {
                ArithmeticGameScreen(
                    onBack = { navController.popBackStack() },
                    onFinish = { score, totalTasks ->
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Screen.GamePuzzle.route,
                enterTransition = { NavigationAnimations.fadeInTransition() },
                exitTransition = { NavigationAnimations.fadeOutTransition() },
                popEnterTransition = { NavigationAnimations.fadeInTransition() },
                popExitTransition = { NavigationAnimations.fadeOutTransition() }
            ) {
                PuzzleGameScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(
                route = Screen.GameSimon.route,
                enterTransition = { NavigationAnimations.fadeInTransition() },
                exitTransition = { NavigationAnimations.fadeOutTransition() },
                popEnterTransition = { NavigationAnimations.fadeInTransition() },
                popExitTransition = { NavigationAnimations.fadeOutTransition() }
            ) {
                SimonGameScreen(
                    onBack = { navController.popBackStack() },
                    onFinish = { score ->
                        navController.popBackStack()
                    }
                )
            }
            
            composable(
                route = Screen.GameUwaga.route,
                enterTransition = { NavigationAnimations.fadeInTransition() },
                exitTransition = { NavigationAnimations.fadeOutTransition() },
                popEnterTransition = { NavigationAnimations.fadeInTransition() },
                popExitTransition = { NavigationAnimations.fadeOutTransition() }
            ) {
                UwagaGameScreen(
                    onBack = { navController.popBackStack() },
                    onFinish = { score, totalTasks ->
                        navController.popBackStack()
                    }
                )
            }
            
            composable(
                route = Screen.Settings.route,
                enterTransition = { NavigationAnimations.fadeInTransition() },
                exitTransition = { NavigationAnimations.fadeOutTransition() },
                popEnterTransition = { NavigationAnimations.fadeInTransition() },
                popExitTransition = { NavigationAnimations.fadeOutTransition() }
            ) {
                SettingsScreen()
            }

            composable(
                route = Screen.GameMemory.route,
                enterTransition = { NavigationAnimations.fadeInTransition() },
                exitTransition = { NavigationAnimations.fadeOutTransition() },
                popEnterTransition = { NavigationAnimations.fadeInTransition() },
                popExitTransition = { NavigationAnimations.fadeOutTransition() }
            ) {
                MemoryScreen(
                    onBack = { navController.popBackStack() },
                    onFinish = { score ->
                    }
                )
            }

            composable(
                route = Screen.GamePary.route,
                enterTransition = { NavigationAnimations.fadeInTransition() },
                exitTransition = { NavigationAnimations.fadeOutTransition() },
                popEnterTransition = { NavigationAnimations.fadeInTransition() },
                popExitTransition = { NavigationAnimations.fadeOutTransition() }
            ) {
                ParyScreen(
                    onBack = { navController.popBackStack() },
                    onFinish = { score ->
                    }
                )
            }
        }
    }
}
