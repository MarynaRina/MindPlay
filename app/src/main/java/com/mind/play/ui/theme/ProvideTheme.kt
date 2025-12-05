package com.mind.play.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.mind.play.data.repository.SettingsRepository
import org.koin.compose.koinInject

@Composable
fun ProvideTheme(
    settingsRepository: SettingsRepository = koinInject(),
    content: @Composable () -> Unit
) {
    val settings by settingsRepository.settings.collectAsState()
    
    MindPlayTheme(
        highContrast = settings.highContrast,
        textSize = settings.textSize,
        content = content
    )
}
