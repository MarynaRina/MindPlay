package com.mind.play.core.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun AnimatedCard(
    modifier: Modifier = Modifier,
    pressScale: Float = 0.92f,
    animationDuration: Int = 100,
    onClick: () -> Unit = {},
    content: @Composable (Modifier) -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressScale else 1f,
        animationSpec = tween(
            durationMillis = animationDuration,
            easing = androidx.compose.animation.core.FastOutSlowInEasing
        ),
        label = "cardScale"
    )
    
    content(
        modifier
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        val released = tryAwaitRelease()
                        isPressed = false
                        if (released) {
                            onClick()
                        }
                    }
                )
            }
    )
}
