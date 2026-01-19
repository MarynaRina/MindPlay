package com.mind.play.ui.games.pary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mind.play.R
import com.mind.play.core.components.AnimatedCard
import com.mind.play.core.components.GameResultScreen
import com.mind.play.core.components.PrimaryButton
import com.mind.play.core.components.SecondaryButton
import com.mind.play.core.sound.SoundManager
import com.mind.play.ui.theme.BackgroundLight
import com.mind.play.ui.theme.ErrorRed
import com.mind.play.ui.theme.MindPlayTheme
import com.mind.play.ui.theme.RubikBold
import com.mind.play.ui.theme.SuccessGreen
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

private val CardDefault = Color.White
private val CardSelected = Color(0xFFE3F2FD)

@Composable
fun ParyScreen(
    onBack: () -> Unit,
    onFinish: (score: Int) -> Unit = {},
    viewModel: ParyViewModel = koinViewModel()
) {
    val gameState by viewModel.gameState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        AnimatedVisibility(
            visible = gameState.gamePhase == ParyGamePhase.INTRO,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            IntroScreen(
                selectedMode = gameState.gridMode,
                onSelectMode = { viewModel.setGridMode(it) },
                onStartClick = { viewModel.startGame() }
            )
        }

        AnimatedVisibility(
            visible = gameState.gamePhase != ParyGamePhase.INTRO && gameState.gamePhase != ParyGamePhase.FINISHED,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            when (gameState.gamePhase) {
                ParyGamePhase.PLAYING -> {
                    GamePlayScreen(
                        gameState = gameState,
                        onCardClick = { viewModel.onCardClick(it) },
                        onPauseClick = { viewModel.togglePause() },
                        onBackClick = onBack,
                        blurred = false
                    )
                }

                ParyGamePhase.PAUSED -> {
                    GamePlayScreen(
                        gameState = gameState,
                        onCardClick = {},
                        onPauseClick = {},
                        onBackClick = {},
                        blurred = true
                    )
                    PauseOverlay(onResume = { viewModel.resumeGame() })
                }

                else -> Unit
            }
        }
        
        AnimatedVisibility(
            visible = gameState.gamePhase == ParyGamePhase.FINISHED,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            val isSuccess = viewModel.isGameSuccessful()
            val timeTaken = if (isSuccess) {
                formatTimeForDisplay(viewModel.getGameDurationSeconds())
            } else null
            
            GameResultScreen(
                isSuccess = isSuccess,
                score = gameState.correctAnswers,
                totalTasks = gameState.totalRounds,
                onPlayAgain = { viewModel.restartGame() },
                onBack = {
                    viewModel.saveGameResult()
                    onFinish(viewModel.getScore())
                    onBack()
                },
                timeTaken = timeTaken
            )
        }
    }
}

@Composable
private fun IntroScreen(
    selectedMode: ParyGridMode,
    onSelectMode: (ParyGridMode) -> Unit,
    onStartClick: () -> Unit,
    soundManager: SoundManager = koinInject()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Pary / Różnice",
            style = MaterialTheme.typography.displayLarge,
            color = MindPlayTheme.colors.textPrimary,
            fontFamily = RubikBold
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Wybierz element, który różni się od pozostałych, lub połącz dwa identyczne elementy. " +
                    "Skup się i znajdź właściwą odpowiedź. Nie ma pośpiechu — graj w swoim tempie.",
            style = MaterialTheme.typography.bodyLarge,
            color = MindPlayTheme.colors.textSecondary,
            modifier = Modifier.padding(end = 8.dp)
        )

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "Tryb planszy:",
            style = MaterialTheme.typography.bodyLarge,
            color = MindPlayTheme.colors.textPrimary,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ModeChip(
                text = "2×4",
                selected = selectedMode == ParyGridMode.GRID_2X4,
                onClick = { onSelectMode(ParyGridMode.GRID_2X4) }
            )
            ModeChip(
                text = "3×4",
                selected = selectedMode == ParyGridMode.GRID_3X4,
                onClick = { onSelectMode(ParyGridMode.GRID_3X4) }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        PrimaryButton(
            text = "ZACZYNAMY",
            onClick = {
                soundManager.playTap()
                onStartClick()
            },
            modifier = Modifier.padding(end = 32.dp)
        )
    }
}

@Composable
private fun ModeChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    soundManager: SoundManager = koinInject()
) {
    val bg = if (selected) Color.White else Color.White.copy(alpha = 0.75f)

    Box(
        modifier = Modifier
            .shadow(
                elevation = if (selected) 10.dp else 4.dp,
                shape = RoundedCornerShape(999.dp),
                clip = false
            )
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    soundManager.playTap()
                    onClick()
                }
            )
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MindPlayTheme.colors.textPrimary,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

@Composable
private fun GamePlayScreen(
    gameState: ParyGameState,
    onCardClick: (Int) -> Unit,
    onPauseClick: () -> Unit,
    onBackClick: () -> Unit,
    blurred: Boolean,
    soundManager: SoundManager = koinInject()
) {
    val columns = gameState.gridMode.columns
    val spacing: Dp = 16.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(if (blurred) Modifier else Modifier)
            .background(BackgroundLight)
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
                    onBackClick()
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

            if (gameState.isStressMode) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Czas:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MindPlayTheme.colors.textSecondary
                    )
                    Text(
                        text = formatTime(gameState.timeRemainingSeconds),
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
                    text = "${gameState.correctAnswers}/${gameState.totalRounds}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MindPlayTheme.colors.textHeading
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = {
                    soundManager.playTap()
                    onPauseClick()
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                horizontalArrangement = Arrangement.spacedBy(spacing),
                verticalArrangement = Arrangement.spacedBy(spacing)
            ) {
                itemsIndexed(gameState.cards) { index, card ->
                    ParyCardItem(
                        card = card,
                        onClick = { onCardClick(index) },
                        modifier = Modifier.aspectRatio(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ParyCardItem(
    card: ParyCard,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardColor = when {
        card.isCorrect -> SuccessGreen
        card.isWrong -> ErrorRed
        card.isSelected -> CardSelected
        else -> CardDefault
    }

    AnimatedCard(
        modifier = modifier,
        onClick = onClick
    ) { animatedModifier ->
        Box(
            modifier = animatedModifier
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(18.dp),
                    clip = false
                )
                .clip(RoundedCornerShape(18.dp))
                .background(cardColor),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = card.iconType.iconRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(0.5f)
            )
        }
    }
}

@Composable
private fun PauseOverlay(
    onResume: () -> Unit,
    soundManager: SoundManager = koinInject()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                soundManager.playTap()
                onResume()
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_continue),
                contentDescription = "Continue",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier
                    .size(200.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        soundManager.playTap()
                        onResume()
                    }
            )
        }
        
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp, start = 48.dp, end = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PrimaryButton(
                text = "GRAJ DALEJ",
                onClick = {
                    soundManager.playTap()
                    onResume()
                },
                modifier = Modifier
            )
        }
    }
}

@Composable
private fun PillButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .shadow(8.dp, RoundedCornerShape(999.dp))
            .clip(RoundedCornerShape(999.dp))
            .background(Color.White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 26.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%d:%02d", minutes, secs)
}

private fun formatTimeForDisplay(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%d:%02d", minutes, secs)
}
