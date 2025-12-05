package com.mind.play.core.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mind.play.ui.theme.ButtonPrimaryBackground
import com.mind.play.ui.theme.InactiveGray
import com.mind.play.ui.theme.RubikBold

@Composable
fun CircularProgressBar(
    current: Int,
    total: Int,
    size: Dp = 200.dp,
    strokeWidth: Dp = 20.dp,
    animationProgress: Float = 1f,
    modifier: Modifier = Modifier
) {
    val progress = ((current.toFloat() / total.toFloat()) * animationProgress).coerceIn(0f, 1f)
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 600),
        label = "circular_progress"
    )
    
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val sweepAngle = 360f * animatedProgress
            
            drawArc(
                color = InactiveGray.copy(alpha = 0.3f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
            
            if (animatedProgress > 0f) {
                drawArc(
                    color = ButtonPrimaryBackground,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
                )
            }
        }
        
        Text(
            text = "${(progress * 100).toInt()}%",
            fontFamily = RubikBold,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = Color.Black
        )
    }
}
