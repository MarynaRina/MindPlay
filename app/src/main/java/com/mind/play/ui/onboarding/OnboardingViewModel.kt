package com.mind.play.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mind.play.data.repository.SettingsRepository
import com.mind.play.ui.theme.TextSize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class OnboardingStep {
    Contrast,
    TextSize,
    StressFree,
    Summary;
    
    fun next(): OnboardingStep = when (this) {
        Contrast -> TextSize
        TextSize -> StressFree
        StressFree -> Summary
        Summary -> Summary
    }
}

data class OnboardingUiState(
    val currentStep: OnboardingStep = OnboardingStep.Contrast,
    val highContrast: Boolean = true,
    val textSize: TextSize = TextSize.MEDIUM,
    val stressMode: Boolean = false,
    val isSaving: Boolean = false,
    val isCompleted: Boolean = false
)

class OnboardingViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()
    
    init {
        viewModelScope.launch {
            val stored = settingsRepository.settings.filterNotNull().first()
            _uiState.update {
                it.copy(
                    highContrast = stored.highContrast,
                    textSize = stored.textSize,
                    stressMode = stored.stressMode
                )
            }
        }
    }
    
    fun onHighContrastChanged(enabled: Boolean) {
        _uiState.update { it.copy(highContrast = enabled) }
    }
    
    fun onTextSizeSelected(size: TextSize) {
        _uiState.update { it.copy(textSize = size) }
    }
    
    fun onStressModeChanged(enabled: Boolean) {
        _uiState.update { it.copy(stressMode = enabled) }
    }
    
    fun onPrimaryAction() {
        val current = _uiState.value
        if (current.isSaving) return
        
        if (current.currentStep == OnboardingStep.Summary) {
            completeOnboarding()
        } else {
            _uiState.update { it.copy(currentStep = current.currentStep.next()) }
        }
    }
    
    private fun completeOnboarding() {
        val current = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            settingsRepository.updateHighContrast(current.highContrast)
            settingsRepository.updateTextSize(current.textSize)
            settingsRepository.updateStressMode(current.stressMode)
            settingsRepository.setOnboardingCompleted(true)
            _uiState.update { it.copy(isSaving = false, isCompleted = true) }
        }
    }
}
