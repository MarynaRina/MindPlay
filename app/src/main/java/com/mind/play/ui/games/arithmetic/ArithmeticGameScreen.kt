package com.mind.play.ui.games.arithmetic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.mind.play.core.components.GameResultScreen
import com.mind.play.core.components.MindPlayProgressBar
import com.mind.play.ui.games.arithmetic.components.PauseOverlay
import com.mind.play.ui.games.arithmetic.components.TaskCard
import com.mind.play.ui.theme.MindPlayTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ArithmeticGameScreen(
    onBack: () -> Unit,
    onFinish: (score: Int, totalTasks: Int) -> Unit,
    viewModel: ArithmeticViewModel = koinViewModel()
) {
    val gameState by viewModel.gameState.collectAsState()
    var showIntro by remember { mutableStateOf(true) }
    
    when {
        showIntro -> {
            ArithmeticIntroScreen(
                onStartGame = {
                    showIntro = false
                    viewModel.startGame()
                }
            )
        }
        gameState.isFinished -> {
            val isSuccess = gameState.score >= (gameState.totalTasks * 0.6).toInt()
            val timeTaken = if (!gameState.stressMode) {
                viewModel.formatTime(120 - gameState.timeLeftSeconds)
            } else null
            
            GameResultScreen(
                isSuccess = isSuccess,
                score = gameState.score,
                totalTasks = gameState.totalTasks,
                timeTaken = timeTaken,
                onPlayAgain = {
                    viewModel.startGame()
                },
                onBack = onBack
            )
        }
        else -> {
            GameContent(
                gameState = gameState,
                viewModel = viewModel,
                onBack = onBack
            )
        }
    }
}

@Composable
private fun GameContent(
    gameState: com.mind.play.domain.models.ArithmeticGameState,
    viewModel: ArithmeticViewModel,
    onBack: () -> Unit
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
                    onClick = onBack,
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
                        text = "${gameState.currentTaskIndex}/${gameState.totalTasks}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MindPlayTheme.colors.textHeading
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                IconButton(
                    onClick = { viewModel.pauseGame() },
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

            MindPlayProgressBar(
                current = gameState.currentTaskIndex,
                total = gameState.totalTasks,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            gameState.currentTask?.let { task ->
                TaskCard(
                    task = task,
                    selectedAnswer = gameState.selectedAnswer,
                    isCorrect = gameState.isCorrect,
                    onAnswerSelected = { answer ->
                        viewModel.selectAnswer(answer)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        if (gameState.isPaused) {
            PauseOverlay(
                onResume = { viewModel.resumeGame() },
                onQuit = onBack
            )
        }
    }
}
