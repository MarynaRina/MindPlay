package com.mind.play.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Custom theme settings
data class MindPlayColors(
    val background: Color,
    val buttonPrimary: Color,
    val buttonPrimaryText: Color,
    val buttonSecondary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textHeading: Color,
    val success: Color,
    val error: Color,
    val cardBlue: Color,
    val inactive: Color,
    val simonGreen: Color,
    val simonOrange: Color,
    val simonPink: Color,
    val simonYellow: Color
)

// High contrast color scheme
private val HighContrastColors = MindPlayColors(
    background = BackgroundLight,
    buttonPrimary = ButtonPrimaryBackground,
    buttonPrimaryText = ButtonPrimaryText,
    buttonSecondary = ButtonSecondaryBackground,
    textPrimary = TextPrimary,
    textSecondary = TextSecondary,
    textHeading = TextPrimary,
    success = SuccessGreen,
    error = ErrorRed,
    cardBlue = CardBlue,
    inactive = InactiveGray,
    simonGreen = SimonGreen,
    simonOrange = SimonOrange,
    simonPink = SimonPink,
    simonYellow = SimonYellow
)

// Low contrast color scheme
private val LowContrastColors = MindPlayColors(
    background = BackgroundLight,
    buttonPrimary = ButtonSecondaryBackground,
    buttonPrimaryText = ButtonPrimaryText,
    buttonSecondary = ButtonSecondaryBackground,
    textPrimary = TextSecondary,
    textSecondary = TextSecondary,
    textHeading = TextLowContrast,
    success = SuccessGreen,
    error = ErrorRed,
    cardBlue = CardBlue,
    inactive = InactiveGray,
    simonGreen = SimonGreen,
    simonOrange = SimonOrange,
    simonPink = SimonPink,
    simonYellow = SimonYellow
)

// CompositionLocal for custom colors
val LocalMindPlayColors = staticCompositionLocalOf { HighContrastColors }

@Composable
fun MindPlayTheme(
    highContrast: Boolean = true,
    textSize: TextSize = TextSize.MEDIUM,
    content: @Composable () -> Unit
) {
    val customColors = if (highContrast) HighContrastColors else LowContrastColors
    
    val colorScheme = lightColorScheme(
        primary = customColors.buttonPrimary,
        onPrimary = customColors.buttonPrimaryText,
        background = customColors.background,
        onBackground = customColors.textPrimary,
        surface = customColors.background,
        onSurface = customColors.textPrimary,
        error = customColors.error,
        onError = ButtonPrimaryText
    )

    CompositionLocalProvider(LocalMindPlayColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = getTypography(textSize),
            content = content
        )
    }
}

// Extension to access custom colors
object MindPlayTheme {
    val colors: MindPlayColors
        @Composable
        get() = LocalMindPlayColors.current
}