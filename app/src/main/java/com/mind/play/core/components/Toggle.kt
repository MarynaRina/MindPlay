package com.mind.play.core.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mind.play.ui.theme.ButtonPrimaryBackground
import com.mind.play.ui.theme.InactiveGray

@Composable
fun MindPlayToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val offsetX by animateDpAsState(
        targetValue = if (checked) 32.dp else 0.dp,
        animationSpec = tween(durationMillis = 200),
        label = "toggle_offset"
    )
    
    Box(
        modifier = modifier
            .width(72.dp)
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Transparent)
            .border(
                width = 2.dp,
                color = if (checked) ButtonPrimaryBackground else InactiveGray,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onCheckedChange(!checked) }
            )
            .padding(4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .offset(x = offsetX)
                .clip(CircleShape)
                .background(if (checked) ButtonPrimaryBackground else InactiveGray)
        )
    }
}
