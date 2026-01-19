package com.mind.play.ui.games.simon.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.mind.play.ui.games.simon.SimonColor
import com.mind.play.ui.theme.ErrorRed
import com.mind.play.ui.theme.SimonGreen
import com.mind.play.ui.theme.SimonOrange
import com.mind.play.ui.theme.SimonPink
import com.mind.play.ui.theme.SimonYellow

@Composable
fun SimonBlock(
    color: SimonColor,
    isHighlighted: Boolean,
    isWrong: Boolean = false,
    isInteractive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val inactiveColor = Color.White
    
    val activeColor = when (color) {
        SimonColor.GREEN -> SimonGreen
        SimonColor.ORANGE -> SimonOrange
        SimonColor.PINK -> SimonPink
        SimonColor.YELLOW -> SimonYellow
    }
    
    var isPressed by remember { mutableStateOf(false) }
    
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isWrong -> ErrorRed
            isHighlighted -> activeColor
            else -> inactiveColor
        },
        animationSpec = tween(durationMillis = 200),
        label = "blockColor"
    )

    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 400f
        ),
        label = "pressScale"
    )

    val highlightScale by animateFloatAsState(
        targetValue = if (isHighlighted) 1.08f else 1f,
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = 300f
        ),
        label = "highlightScale"
    )
    
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .scale(pressScale * highlightScale)
            .then(
                if (isInteractive) {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isPressed = true
                                tryAwaitRelease()
                                isPressed = false
                            },
                            onTap = { onClick() }
                        )
                    }
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {}
}
