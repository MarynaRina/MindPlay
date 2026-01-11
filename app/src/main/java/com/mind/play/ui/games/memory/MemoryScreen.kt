package com.mind.play.ui.games.memory

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mind.play.R
import com.mind.play.core.components.PrimaryButton
import com.mind.play.core.components.SecondaryButton
import com.mind.play.ui.theme.BackgroundLight
import com.mind.play.ui.theme.CardBlue
import com.mind.play.ui.theme.MindPlayTheme
import com.mind.play.ui.theme.RubikBold
import com.mind.play.ui.theme.SuccessGreen
import org.koin.androidx.compose.koinViewModel

private val MismatchRed = Color(0xFFF06A6A)

@Composable
fun MemoryScreen(
    onBack: () -> Unit,
    onFinish: (score: Int) -> Unit = {},
    viewModel: MemoryViewModel = koinViewModel()
) {
    val gameState by viewModel.gameState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        when (gameState.gamePhase) {
            MemoryGamePhase.INTRO -> {
                IntroScreen(
                    selectedMode = gameState.gridMode,
                    onSelectMode = { viewModel.setGridMode(it) },
                    onStartClick = { viewModel.startGame() }
                )
            }

            MemoryGamePhase.PLAYING -> {
                GamePlayScreen(
                    gameState = gameState,
                    onCardClick = { viewModel.onCardClick(it) },
                    onPauseClick = { viewModel.togglePause() },
                    onBackClick = onBack,
                    blurred = false
                )
            }

            MemoryGamePhase.PAUSED -> {
                GamePlayScreen(
                    gameState = gameState,
                    onCardClick = {},
                    onPauseClick = {},
                    onBackClick = {},
                    blurred = true
                )
                PauseOverlay(onResume = { viewModel.resumeGame() })
            }

            MemoryGamePhase.ROUND_COMPLETE -> {
                GamePlayScreen(
                    gameState = gameState,
                    onCardClick = {},
                    onPauseClick = {},
                    onBackClick = {},
                    blurred = true
                )
                RoundCompleteOverlay(onNextRound = { viewModel.nextRound() })
            }

            MemoryGamePhase.FINISHED -> {
                FinishedScreen(
                    isSuccess = viewModel.isGameSuccessful(),
                    correctAnswers = gameState.correctAnswers,
                    totalPairs = gameState.totalRounds * gameState.totalPairs,
                    durationSeconds = viewModel.getGameDurationSeconds(),
                    onPlayAgain = { viewModel.restartGame() },
                    onBack = {
                        viewModel.saveGameResult()
                        onFinish(viewModel.getScore())
                        onBack()
                    }
                )
            }
        }
    }
}

@Composable
private fun IntroScreen(
    selectedMode: MemoryGridMode,
    onSelectMode: (MemoryGridMode) -> Unit,
    onStartClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Memory",
            style = MaterialTheme.typography.displayLarge,
            color = MindPlayTheme.colors.textPrimary,
            fontFamily = RubikBold
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Odsłaniaj po dwa pola i staraj się znaleźć pasujące pary. " +
                    "Zapamiętaj ich położenie i dopasuj wszystkie pary, aby ukończyć rundę.",
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
                selected = selectedMode == MemoryGridMode.GRID_2X4,
                onClick = { onSelectMode(MemoryGridMode.GRID_2X4) }
            )
            ModeChip(
                text = "3×4",
                selected = selectedMode == MemoryGridMode.GRID_3X4,
                onClick = { onSelectMode(MemoryGridMode.GRID_3X4) }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        PrimaryButton(
            text = "ZACZYNAMY",
            onClick = onStartClick,
            modifier = Modifier.padding(end = 32.dp)
        )
    }
}

@Composable
private fun ModeChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (selected) Color.White else Color.White.copy(alpha = 0.75f)
    val border = if (selected) MindPlayTheme.colors.textPrimary.copy(alpha = 0.15f) else Color.Transparent

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
                onClick = onClick
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
    gameState: MemoryGameState,
    onCardClick: (Int) -> Unit,
    onPauseClick: () -> Unit,
    onBackClick: () -> Unit,
    blurred: Boolean
) {
    val columns = gameState.gridMode.columns
    val cardSize: Dp = if (columns == 2) 150.dp else 110.dp
    val spacing: Dp = if (columns == 2) 14.dp else 12.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(if (blurred) Modifier.blur(10.dp) else Modifier)
            .padding(16.dp)
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Back",
                    tint = MindPlayTheme.colors.textPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (gameState.isStressMode) {
                    Text(
                        text = "Czas: ${formatTime(gameState.timeRemainingSeconds)}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MindPlayTheme.colors.textPrimary,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text(
                        text = "Progres:",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MindPlayTheme.colors.textPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "${gameState.currentRound}/${gameState.totalRounds}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MindPlayTheme.colors.textSecondary
                )
            }

            IconButton(onClick = onPauseClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_pause),
                    contentDescription = "Pause",
                    tint = MindPlayTheme.colors.textPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            itemsIndexed(gameState.cards) { index, card ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    MemoryCardItem(
                        card = card,
                        onClick = { onCardClick(index) },
                        size = cardSize
                    )
                }
            }
        }
    }
}

@Composable
private fun MemoryCardItem(
    card: MemoryCard,
    onClick: () -> Unit,
    size: Dp
) {
    val rotation by animateFloatAsState(
        targetValue = if (card.isFlipped || card.isMatched) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "card_rotation"
    )

    val cardColor = when {
        card.isMatched -> SuccessGreen
        card.isMismatched -> MismatchRed
        else -> CardBlue
    }

    Box(
        modifier = Modifier
            .size(size)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(18.dp),
                clip = false
            )
            .clip(RoundedCornerShape(18.dp))
            .background(cardColor)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (rotation > 90f) {
            Image(
                painter = painterResource(id = card.iconType.iconRes),
                contentDescription = null,
                modifier = Modifier
                    .size(if (size.value <= 105f) 50.dp else 54.dp)
                    .graphicsLayer { rotationY = 180f }
            )
        }
    }
}

@Composable
private fun PauseOverlay(
    onResume: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.35f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.ic_play),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(96.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            PillButton(
                text = "GRAJ DALEJ",
                onClick = onResume
            )
        }
    }
}

@Composable
private fun RoundCompleteOverlay(
    onNextRound: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.35f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.ic_play),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(96.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            PillButton(
                text = "GRAJ DALEJ",
                onClick = onNextRound
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

@Composable
private fun FinishedScreen(
    isSuccess: Boolean,
    correctAnswers: Int,
    totalPairs: Int,
    durationSeconds: Int,
    onPlayAgain: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        if (isSuccess) {
            Text(
                text = "Brawo! 🎉",
                style = MaterialTheme.typography.displayLarge,
                color = MindPlayTheme.colors.textPrimary,
                fontFamily = RubikBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Udało Ci się ukończyć grę!",
                style = MaterialTheme.typography.titleMedium,
                color = MindPlayTheme.colors.textPrimary
            )
        } else {
            Text(
                text = "Spróbuj\njeszcze raz",
                style = MaterialTheme.typography.displayLarge,
                color = MindPlayTheme.colors.textPrimary,
                fontFamily = RubikBold,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Nie udało się tym razem, ale możesz spróbować ponownie. " +
                        "Każda próba to ćwiczenie i postęp.",
                style = MaterialTheme.typography.bodyLarge,
                color = MindPlayTheme.colors.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Świetna robota! 👍 Oto Twój wynik:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MindPlayTheme.colors.textSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Czas:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MindPlayTheme.colors.textSecondary
                )
                Text(
                    text = formatTimeDetailed(durationSeconds),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MindPlayTheme.colors.textPrimary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Poprawne odpowiedzi:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MindPlayTheme.colors.textSecondary
                )
                Text(
                    text = "$correctAnswers/$totalPairs",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MindPlayTheme.colors.textPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        PrimaryButton(
            text = "GRAJ DALEJ",
            onClick = onPlayAgain,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        SecondaryButton(
            text = "POWRÓT",
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%d:%02d", minutes, secs)
}

private fun formatTimeDetailed(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return if (minutes > 0) {
        "$minutes minuty $secs sekund"
    } else {
        "$secs sekund"
    }
}
