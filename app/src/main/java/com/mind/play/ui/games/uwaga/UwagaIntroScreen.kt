package com.mind.play.ui.games.uwaga

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
import androidx.compose.ui.unit.dp
import com.mind.play.core.components.PrimaryButton
import com.mind.play.core.sound.SoundManager
import com.mind.play.ui.theme.MindPlayTheme
import org.koin.compose.koinInject

@Composable
fun UwagaIntroScreen(
    onStartGame: () -> Unit,
    modifier: Modifier = Modifier,
    soundManager: SoundManager = koinInject()
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
                .height(460.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Uwaga /\nReakcja",
                style = MaterialTheme.typography.displayLarge,
                color = MindPlayTheme.colors.textHeading
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Reaguj na odpowiedni sygnał — dotknij właściwego elementu, gdy pojawi się na ekranie. Gra trenuje szybkość reakcji, ale możesz grać bez stresu.",
                style = MaterialTheme.typography.bodyLarge,
                color = MindPlayTheme.colors.textSecondary
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            PrimaryButton(
                text = "ZACZYNAMY",
                onClick = {
                    soundManager.playTap()
                    onStartGame()
                },
                modifier = Modifier
            )
        }
    }
}
