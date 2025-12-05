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
    val multiplier = when (textSize) {
        TextSize.SMALL -> 0.85f
        TextSize.MEDIUM -> 1f
        TextSize.LARGE -> 1.15f
    }

    return Typography(
        displayLarge = TextStyle(
            fontFamily = RubikBold,
            fontWeight = FontWeight.Bold,
            fontSize = (32 * multiplier).sp,
            lineHeight = (40 * multiplier).sp
        ),
        bodyLarge = TextStyle(
            fontFamily = InterRegular,
            fontWeight = FontWeight.Normal,
            fontSize = (20 * multiplier).sp,
            lineHeight = (28 * multiplier).sp
        ),
        bodyMedium = TextStyle(
            fontFamily = InterRegular,
            fontWeight = FontWeight.Normal,
            fontSize = (16 * multiplier).sp,
            lineHeight = (24 * multiplier).sp
        ),
        labelLarge = TextStyle(
            fontFamily = RubikMedium,
            fontWeight = FontWeight.Medium,
            fontSize = (24 * multiplier).sp,
            lineHeight = (32 * multiplier).sp,
            letterSpacing = 1.sp
        ),
        titleLarge = TextStyle(
            fontFamily = RubikBold,
            fontWeight = FontWeight.Bold,
            fontSize = (32 * multiplier).sp,
            lineHeight = (40 * multiplier).sp
        ),
        titleMedium = TextStyle(
            fontFamily = RubikMedium,
            fontWeight = FontWeight.Medium,
            fontSize = (24 * multiplier).sp,
            lineHeight = (32 * multiplier).sp
        )
    )
}

enum class TextSize {
    SMALL,
    MEDIUM,
    LARGE
}