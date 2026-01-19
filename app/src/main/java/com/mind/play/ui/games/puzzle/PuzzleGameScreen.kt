package com.mind.play.ui.games.puzzle

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mind.play.R
import com.mind.play.core.components.GameResultMetric
import com.mind.play.core.components.GameResultScreen
import com.mind.play.core.components.PrimaryButton
import com.mind.play.core.sound.SoundManager
import com.mind.play.ui.games.arithmetic.components.PauseOverlay
import com.mind.play.ui.games.puzzle.components.PuzzleBoard
import com.mind.play.ui.theme.MindPlayTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun PuzzleGameScreen(
    onBack: () -> Unit,
    viewModel: PuzzleViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showIntro by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = showIntro,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            PuzzleIntroScreen(
                onStartGame = { selectedSize ->
                    showIntro = false
                    viewModel.startGame(selectedSize)
                }
            )
        }
        
        AnimatedVisibility(
            visible = !showIntro,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            when {
                state.isFinished -> {
                    val metrics = mutableListOf<GameResultMetric>()

                    metrics.add(GameResultMetric("Liczba ruchów:", state.moves.toString()))

                    GameResultScreen(
                        isSuccess = state.isWin,
                        score = state.score,
                        totalTasks = 1,
                        customMetrics = metrics,
                        onPlayAgain = {
                            viewModel.startGame(state.gridSize)
                        },
                        onBack = onBack,
                        failureTitle = "Czas minął!",
                        failureMessage = "Niestety czas dobiegł końca. Spróbuj ułożyć puzzle szybciej następnym razem!"
                    )
                }
                else -> {
                    PuzzleContent(
                        state = state,
                        viewModel = viewModel,
                        onBack = onBack
                    )
                }
            }
        }
    }
}

@Composable
private fun PuzzleContent(
    state: PuzzleGameState,
    viewModel: PuzzleViewModel,
    onBack: () -> Unit,
    soundManager: SoundManager = koinInject()
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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

                if (!state.stressMode) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Czas:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MindPlayTheme.colors.textSecondary
                        )
                        Text(
                            text = viewModel.formatTime(state.timeLeftSeconds),
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
                        text = "Ruchy:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MindPlayTheme.colors.textSecondary
                    )
                    Text(
                        text = "${state.moves}",
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

            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(0.85f)
                    .aspectRatio(1f)
                    .border(1.dp, Color.Black),
                contentAlignment = Alignment.Center
            ) {
                PuzzleBoard(
                    tiles = state.tiles,
                    gridSize = state.gridSize,
                    onTileClick = viewModel::onTileClick,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = "OD NOWA",
                onClick = {
                    soundManager.playTap()
                    viewModel.restartGame()
                },
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
            )
        }

        if (state.isPaused) {
            PauseOverlay(
                onResume = { viewModel.resumeGame() },
                onQuit = onBack
            )
        }
    }
}