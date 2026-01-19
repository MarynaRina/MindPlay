package com.mind.play.ui.games.uwaga

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import com.mind.play.ui.games.uwaga.components.UwagaBlock
import com.mind.play.ui.games.uwaga.components.UwagaPauseOverlay
import com.mind.play.ui.theme.MindPlayTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun UwagaGameScreen(
    onBack: () -> Unit,
    onFinish: (score: Int, totalTasks: Int) -> Unit,
    viewModel: UwagaViewModel = koinViewModel()
) {
    val gameState by viewModel.gameState.collectAsState()
    var showIntro by remember { mutableStateOf(true) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = showIntro,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            UwagaIntroScreen(
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
                    val isSuccess = viewModel.isGameSuccess()
                    val timeTaken = viewModel.formatDuration(viewModel.getGameDuration())
                    
                    GameResultScreen(
                        isSuccess = isSuccess,
                        score = gameState.correctCount,
                        totalTasks = gameState.totalTasks,
                        onPlayAgain = {
                            viewModel.startGame()
                        },
                        onBack = onBack,
                        customMetrics = listOf(
                            GameResultMetric(
                                label = "Czas:",
                                value = timeTaken
                            ),
                            GameResultMetric(
                                label = "Poprawne odpowiedzi:",
                                value = "${gameState.correctCount}/${gameState.totalTasks}"
                            )
                        )
                    )
                }
                else -> {
                    UwagaGameContent(
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
private fun UwagaGameContent(
    gameState: UwagaGameState,
    viewModel: UwagaViewModel,
    onBack: () -> Unit,
    soundManager: SoundManager = koinInject()
) {
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

                if (!gameState.stressMode) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Czas:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MindPlayTheme.colors.textSecondary
                        )
                        Text(
                            text = viewModel.formatTime(gameState.timeLeftSeconds),
                            style = MaterialTheme.typography.titleMedium,
                            color = MindPlayTheme.colors.textHeading
                        )
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                }
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Progres:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MindPlayTheme.colors.textSecondary
                    )
                    Text(
                        text = "${gameState.correctCount}/${gameState.totalTasks}",
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
            
            Spacer(modifier = Modifier.height(24.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 16.dp, top = 16.dp)
            ) {
                items(15) { index ->
                    UwagaBlock(
                        isActive = gameState.activeBlockIndex == index,
                        isWrongTap = gameState.wrongTapIndex == index,
                        onClick = { viewModel.onBlockTapped(index) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
        }

        if (gameState.isPaused) {
            UwagaPauseOverlay(
                onResume = { viewModel.resumeGame() }
            )
        }
    }
}
