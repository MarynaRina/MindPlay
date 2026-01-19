package com.mind.play.ui.games.puzzle

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mind.play.core.components.MindPlayToggle
import com.mind.play.core.components.PrimaryButton
import com.mind.play.ui.theme.MindPlayTheme

@Composable
fun PuzzleIntroScreen(
    onStartGame: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var isHardMode by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .height(520.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Puzzle",
                style = MaterialTheme.typography.displayLarge,
                color = MindPlayTheme.colors.textHeading
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Przesuwaj kafelki, aby ułożyć obrazek w prawidłowej kolejności. Jedno pole jest puste — wykorzystaj je, aby przesuwać pozostałe elementy.",
                style = MaterialTheme.typography.bodyLarge,
                color = MindPlayTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(20.dp))
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Trudny poziom",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = MindPlayTheme.colors.textHeading
                        )
                        Text(
                            text = if (isHardMode) "Siatka 4x4 (15 klocków)" else "Siatka 3x3 (8 klocków)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MindPlayTheme.colors.textSecondary
                        )
                    }

                    MindPlayToggle(
                        checked = isHardMode,
                        onCheckedChange = { isHardMode = it }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = "ZACZYNAMY",
                onClick = {
                    onStartGame(if (isHardMode) 4 else 3)
                }
            )
        }
    }
}