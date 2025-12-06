package com.mind.play.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mind.play.core.components.PrimaryButton
import com.mind.play.ui.theme.MindPlayTheme

@Composable
fun WelcomeScreen(
    onStartClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .height(460.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Witaj w\nMindPlay!",
                style = MaterialTheme.typography.displayLarge,
                color = MindPlayTheme.colors.textPrimary,
                textAlign = TextAlign.Start
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Aplikacja pomaga ćwiczyć pamięć, koncentrację i sprawność  umysłową — spokojnie, bez presji czasu.",
                style = MaterialTheme.typography.bodyLarge,
                color = MindPlayTheme.colors.textSecondary,
                textAlign = TextAlign.Start
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            PrimaryButton(
                text = "Zaczynamy",
                onClick = onStartClick
            )
        }
    }
}
