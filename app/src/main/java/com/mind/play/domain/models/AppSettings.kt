package com.mind.play.domain.models

import com.mind.play.ui.theme.TextSize

data class AppSettings(
    val highContrast: Boolean = true,
    val textSize: TextSize = TextSize.MEDIUM,
    val stressMode: Boolean = false,
    val uiSoundEnabled: Boolean = true,
    val gameSoundEnabled: Boolean = true,
    val notificationsEnabled: Boolean = false,
    val onboardingCompleted: Boolean = false
)
