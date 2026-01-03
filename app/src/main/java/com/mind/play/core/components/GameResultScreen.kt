package com.mind.play.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mind.play.ui.theme.MindPlayTheme


data class GameResultMetric(
    val label: String,
    val value: String
)

@Composable
fun GameResultScreen(
    isSuccess: Boolean,
    score: Int,
    totalTasks: Int,
    onPlayAgain: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    timeTaken: String? = null,
    successTitle: String = "Brawo! 🎉\nUdało Ci się\nukończyć grę!",
    failureTitle: String = "Spróbuj\njeszcze raz",
    successMessage: String = "Świetna robota! 🙌 Oto Twój wynik:",
    failureMessage: String = "Nie udało się tym razem, ale możesz spróbować ponownie. Każda próba to ćwiczenie i postęp.",
    playAgainButtonText: String = "GRAJ DALEJ",
    backButtonText: String = "POWRÓT",
    customMetrics: List<GameResultMetric>? = null,
    scoreLabel: String = "Poprawne odpowiedzi:",
    timeLabel: String = "Czas:"
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .height(600.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = if (isSuccess) successTitle else failureTitle,
                style = MaterialTheme.typography.displayLarge,
                color = MindPlayTheme.colors.textHeading
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (isSuccess) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFE3F2FD)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = successMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MindPlayTheme.colors.textHeading
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (customMetrics != null) {
                            customMetrics.forEachIndexed { index, metric ->
                                ResultItem(label = metric.label, value = metric.value)
                                if (index < customMetrics.size - 1) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        } else {
                            timeTaken?.let {
                                ResultItem(label = timeLabel, value = it)
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            ResultItem(
                                label = scoreLabel,
                                value = "$score/$totalTasks"
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = failureMessage,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MindPlayTheme.colors.textSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            PrimaryButton(
                text = playAgainButtonText,
                onClick = onPlayAgain
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SecondaryButton(
                text = backButtonText,
                onClick = onBack
            )
        }
    }
}

@Composable
private fun ResultItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MindPlayTheme.colors.textSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MindPlayTheme.colors.textHeading
        )
    }
}
