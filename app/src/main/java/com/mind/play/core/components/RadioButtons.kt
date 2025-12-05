package com.mind.play.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mind.play.ui.theme.ButtonPrimaryBackground
import com.mind.play.ui.theme.InactiveGray
import com.mind.play.ui.theme.InterRegular
import com.mind.play.ui.theme.MindPlayTheme

@Composable
fun MindPlayRadioButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.Transparent)
                .border(
                    width = 2.dp,
                    color = InactiveGray,
                    shape = CircleShape
                )
                .clickable(onClick = onClick)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(ButtonPrimaryBackground)
                )
            }
        }
        
        Text(
            text = label,
            fontFamily = InterRegular,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            color = MindPlayTheme.colors.textPrimary,
            modifier = Modifier.clickable(onClick = onClick)
        )
    }
}
