package com.mind.play.data.repository

import com.mind.play.domain.models.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository {
    
    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()
    
    suspend fun updateHighContrast(enabled: Boolean) {
        _settings.value = _settings.value.copy(highContrast = enabled)
    }
    
    suspend fun updateTextSize(textSize: com.mind.play.ui.theme.TextSize) {
        _settings.value = _settings.value.copy(textSize = textSize)
    }
    
    suspend fun updateStressMode(enabled: Boolean) {
        _settings.value = _settings.value.copy(stressMode = enabled)
    }
    
    suspend fun updateUiSound(enabled: Boolean) {
        _settings.value = _settings.value.copy(uiSoundEnabled = enabled)
    }
    
    suspend fun updateGameSound(enabled: Boolean) {
        _settings.value = _settings.value.copy(gameSoundEnabled = enabled)
    }
    
    suspend fun updateNotifications(enabled: Boolean) {
        _settings.value = _settings.value.copy(notificationsEnabled = enabled)
    }
}
