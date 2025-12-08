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


@Composable
fun GameResultScreen(
    isSuccess: Boolean,
    score: Int,
    totalTasks: Int,
    timeTaken: String? = null,
    onPlayAgain: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
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
                text = if (isSuccess) {
                    "Brawo! 🎉\nUdało Ci się\nukończyć grę!"
                } else {
                    "Spróbuj\njeszcze raz"
                },
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
                            text = "Świetna robota! 🙌 Oto Twój wynik:",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MindPlayTheme.colors.textHeading
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        timeTaken?.let {
                            ResultItem(label = "Czas:", value = it)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        
                        ResultItem(
                            label = "Poprawne odpowiedzi:",
                            value = "$score/$totalTasks"
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Nie udało się tym razem, ale możesz spróbować ponownie. Każda próba to ćwiczenie i postęp.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MindPlayTheme.colors.textSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            PrimaryButton(
                text = "GRAJ DALEJ",
                onClick = onPlayAgain
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SecondaryButton(
                text = "POWRÓT",
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
            style = MaterialTheme.typography.titleLarge,
            color = MindPlayTheme.colors.textHeading
        )
    }
}
