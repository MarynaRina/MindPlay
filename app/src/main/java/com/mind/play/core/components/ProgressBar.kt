package com.mind.play.core.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mind.play.ui.theme.InactiveGray
import com.mind.play.ui.theme.MindPlayTheme

@Composable
fun MindPlayProgressBar(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    val progress = (current.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300),
        label = "progress_animation"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(InactiveGray.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MindPlayTheme.colors.buttonPrimary)
        )
    }
}
