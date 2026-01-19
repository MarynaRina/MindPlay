package com.mind.play.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mind.play.core.notifications.NotificationScheduler
import com.mind.play.data.repository.SettingsRepository
import com.mind.play.ui.theme.TextSize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val highContrast: Boolean = true,
    val textSize: TextSize = TextSize.MEDIUM,
    val stressMode: Boolean = false,
    val uiSoundEnabled: Boolean = true,
    val gameSoundEnabled: Boolean = true,
    val notificationsEnabled: Boolean = false
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        viewModelScope.launch {
            settingsRepository.settings
                .filterNotNull()
                .collect { settings ->
                    _uiState.update {
                        it.copy(
                            highContrast = settings.highContrast,
                            textSize = settings.textSize,
                            stressMode = settings.stressMode,
                            uiSoundEnabled = settings.uiSoundEnabled,
                            gameSoundEnabled = settings.gameSoundEnabled,
                            notificationsEnabled = settings.notificationsEnabled
                        )
                    }
                }
        }
    }

    fun onHighContrastToggled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateHighContrast(enabled)
        }
    }

    fun onTextSizeSelected(size: TextSize) {
        viewModelScope.launch {
            settingsRepository.updateTextSize(size)
        }
    }

    fun onStressModeToggled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateStressMode(enabled)
        }
    }

    fun onUiSoundToggled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateUiSound(enabled)
        }
    }

    fun onGameSoundToggled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateGameSound(enabled)
        }
    }

    fun onNotificationsToggled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateNotifications(enabled)

            // Планувати або скасовувати нагадування
            if (enabled) {
                notificationScheduler.scheduleDailyReminder()
            } else {
                notificationScheduler.cancelDailyReminder()
            }
        }
    }
}
