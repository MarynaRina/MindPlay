package com.mind.play.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mind.play.R

val RubikBold = FontFamily(
    Font(R.font.rubik_bold, FontWeight.Bold)
)

val RubikMedium = FontFamily(
    Font(R.font.rubik_medium, FontWeight.Medium)
)

val InterRegular = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal)
)

fun getTypography(textSize: TextSize = TextSize.MEDIUM): Typography {
    val offset = when (textSize) {
        TextSize.SMALL -> -5
        TextSize.MEDIUM -> 0
        TextSize.LARGE -> 5
    }
    
    fun adjusted(value: Int) = (value + offset).coerceAtLeast(8).sp

    return Typography(
        displayLarge = TextStyle(
            fontFamily = RubikBold,
            fontWeight = FontWeight.Bold,
            fontSize = adjusted(40),
            lineHeight = adjusted(48)
        ),
        bodyLarge = TextStyle(
            fontFamily = InterRegular,
            fontWeight = FontWeight.Normal,
            fontSize = adjusted(18),
            lineHeight = adjusted(26)
        ),
        bodyMedium = TextStyle(
            fontFamily = InterRegular,
            fontWeight = FontWeight.Normal,
            fontSize = adjusted(16),
            lineHeight = adjusted(22)
        ),
        labelLarge = TextStyle(
            fontFamily = RubikMedium,
            fontWeight = FontWeight.Medium,
            fontSize = adjusted(20),
            lineHeight = adjusted(28),
            letterSpacing = 1.sp
        ),
        titleLarge = TextStyle(
            fontFamily = RubikBold,
            fontWeight = FontWeight.Bold,
            fontSize = adjusted(32),
            lineHeight = adjusted(40)
        ),
        titleMedium = TextStyle(
            fontFamily = RubikMedium,
            fontWeight = FontWeight.Medium,
            fontSize = adjusted(24),
            lineHeight = adjusted(32)
        )
    )
}

enum class TextSize {
    SMALL,
    MEDIUM,
    LARGE
}
