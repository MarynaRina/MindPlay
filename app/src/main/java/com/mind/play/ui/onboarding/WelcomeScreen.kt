package com.mind.play.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mind.play.ui.theme.InterRegular
import com.mind.play.ui.theme.MindPlayTheme
import com.mind.play.ui.theme.RubikBold

@Composable
fun WelcomeScreen(
    onStartClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Witaj w\nMindPlay!",
            style = MaterialTheme.typography.displayLarge.copy(
                fontFamily = RubikBold,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                lineHeight = 48.sp
            ),
            color = MindPlayTheme.colors.textPrimary,
            textAlign = TextAlign.Start
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Aplikacja pomaga ćwiczyć pamięć, koncentrację i sprawność umysłową — spokojnie, bez presji czasu.",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = InterRegular,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                lineHeight = 26.sp
            ),
            color = MindPlayTheme.colors.textSecondary,
            textAlign = TextAlign.Start
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onStartClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MindPlayTheme.colors.buttonPrimary,
                contentColor = MindPlayTheme.colors.buttonPrimaryText
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "ZACZYNAMY",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp
                )
            )
        }
    }
}
