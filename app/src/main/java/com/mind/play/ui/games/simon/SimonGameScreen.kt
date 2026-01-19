package com.mind.play.ui.games.simon

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mind.play.R
import com.mind.play.core.components.GameResultMetric
import com.mind.play.core.components.GameResultScreen
import com.mind.play.core.sound.SoundManager
import com.mind.play.ui.games.simon.components.SimonBlock
import com.mind.play.ui.games.simon.components.SimonPauseOverlay
import com.mind.play.ui.theme.MindPlayTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun SimonGameScreen(
    onBack: () -> Unit,
    onFinish: (score: Int) -> Unit,
    viewModel: SimonViewModel = koinViewModel()
) {
    val gameState by viewModel.gameState.collectAsState()
    var showIntro by remember { mutableStateOf(true) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = showIntro,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            SimonIntroScreen(
                onStartGame = {
                    showIntro = false
                    viewModel.startGame()
                }
            )
        }
        
        AnimatedVisibility(
            visible = !showIntro,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            when {
                gameState.isFinished -> {
                    val completedRounds = viewModel.getCompletedRounds()
                    val timeTaken = viewModel.formatTime(viewModel.getGameDuration())
                    
                    GameResultScreen(
                        isSuccess = true,
                        score = completedRounds,
                        totalTasks = completedRounds,
                        onPlayAgain = {
                            viewModel.startGame()
                        },
                        onBack = onBack,
                        successTitle = "Udało się!",
                        successMessage = "Świetna robota! 🙌 Oto Twój wynik:",
                        customMetrics = listOf(
                            GameResultMetric(
                                label = "Czas:",
                                value = timeTaken
                            ),
                            GameResultMetric(
                                label = "Ukończone rundy:",
                                value = completedRounds.toString()
                            )
                        )
                    )
                }
                else -> {
                    SimonGameContent(
                        gameState = gameState,
                        viewModel = viewModel,
                        onBack = onBack
                    )
                }
            }
        }
    }
}

@Composable
private fun SimonGameContent(
    gameState: SimonGameState,
    viewModel: SimonViewModel,
    onBack: () -> Unit,
    soundManager: SoundManager = koinInject()
) {
    val isPlayerTurn = gameState.phase == SimonGamePhase.PLAYER_INPUT
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(top = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        soundManager.playTap()
                        onBack()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MindPlayTheme.colors.textHeading,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Progres:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MindPlayTheme.colors.textSecondary
                    )
                    Text(
                        text = "${gameState.currentRound}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MindPlayTheme.colors.textHeading
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                IconButton(
                    onClick = {
                        soundManager.playTap()
                        viewModel.pauseGame()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pause),
                        contentDescription = "Pause",
                        tint = MindPlayTheme.colors.textHeading,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SimonBlock(
                        color = SimonColor.GREEN,
                        isHighlighted = gameState.highlightedColor == SimonColor.GREEN || 
                                       (isPlayerTurn && gameState.highlightedColor == SimonColor.GREEN),
                        isWrong = gameState.wrongColor == SimonColor.GREEN,
                        isInteractive = isPlayerTurn && !gameState.isPaused,
                        onClick = { viewModel.onColorPressed(SimonColor.GREEN) },
                        modifier = Modifier.weight(1f)
                    )
                    
                    SimonBlock(
                        color = SimonColor.ORANGE,
                        isHighlighted = gameState.highlightedColor == SimonColor.ORANGE ||
                                       (isPlayerTurn && gameState.highlightedColor == SimonColor.ORANGE),
                        isWrong = gameState.wrongColor == SimonColor.ORANGE,
                        isInteractive = isPlayerTurn && !gameState.isPaused,
                        onClick = { viewModel.onColorPressed(SimonColor.ORANGE) },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SimonBlock(
                        color = SimonColor.PINK,
                        isHighlighted = gameState.highlightedColor == SimonColor.PINK ||
                                       (isPlayerTurn && gameState.highlightedColor == SimonColor.PINK),
                        isWrong = gameState.wrongColor == SimonColor.PINK,
                        isInteractive = isPlayerTurn && !gameState.isPaused,
                        onClick = { viewModel.onColorPressed(SimonColor.PINK) },
                        modifier = Modifier.weight(1f)
                    )
                    
                    SimonBlock(
                        color = SimonColor.YELLOW,
                        isHighlighted = gameState.highlightedColor == SimonColor.YELLOW ||
                                       (isPlayerTurn && gameState.highlightedColor == SimonColor.YELLOW),
                        isWrong = gameState.wrongColor == SimonColor.YELLOW,
                        isInteractive = isPlayerTurn && !gameState.isPaused,
                        onClick = { viewModel.onColorPressed(SimonColor.YELLOW) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
        }

        if (gameState.isPaused) {
            SimonPauseOverlay(
                onResume = { viewModel.resumeGame() }
            )
        }
    }
}
