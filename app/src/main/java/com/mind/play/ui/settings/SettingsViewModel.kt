package com.mind.play.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val stressMode: Boolean = false
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
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
                            stressMode = settings.stressMode
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
}
