package com.mind.play.core.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mind.play.ui.theme.ButtonPrimaryBackground
import com.mind.play.ui.theme.ButtonPrimaryText
import com.mind.play.ui.theme.InactiveGray
import com.mind.play.ui.theme.RubikMedium

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ButtonPrimaryBackground,
            contentColor = ButtonPrimaryText
        ),
        shape = RoundedCornerShape(32.dp)
    ) {
        Text(
            text = text.uppercase(),
            fontFamily = RubikMedium,
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = InactiveGray,
            contentColor = ButtonPrimaryText
        ),
        shape = RoundedCornerShape(32.dp)
    ) {
        Text(
            text = text.uppercase(),
            fontFamily = RubikMedium,
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            letterSpacing = 1.sp
        )
    }
}
