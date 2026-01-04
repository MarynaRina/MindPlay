package com.mind.play.ui.games.puzzle

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
import com.mind.play.ui.games.arithmetic.components.PauseOverlay
import com.mind.play.ui.games.puzzle.components.PuzzleBoard
import com.mind.play.ui.theme.MindPlayTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun PuzzleGameScreen(
    onBack: () -> Unit,
    viewModel: PuzzleViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showIntro by remember { mutableStateOf(true) }

    when {
        showIntro -> {
            PuzzleIntroScreen(
                onStartGame = { selectedSize ->
                    showIntro = false
                    viewModel.startGame(selectedSize)
                }
            )
        }
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

@Composable
private fun PuzzleContent(
    state: PuzzleGameState,
    viewModel: PuzzleViewModel,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MindPlayTheme.colors.textHeading
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (!state.stressMode) {
                    Text(
                        text = "Czas: ${viewModel.formatTime(state.timeLeftSeconds)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MindPlayTheme.colors.textHeading
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = { viewModel.pauseGame() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pause),
                        contentDescription = "Pause",
                        tint = MindPlayTheme.colors.textHeading
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .border(1.dp, Color.Black)
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
                onClick = { viewModel.restartGame() },
                modifier = Modifier.padding(bottom = 16.dp)
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