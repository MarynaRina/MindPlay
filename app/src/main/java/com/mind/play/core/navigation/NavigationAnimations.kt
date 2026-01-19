package com.mind.play.core.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

object NavigationAnimations {
    const val DEFAULT_DURATION = 400
    const val FADE_DURATION = 300
    const val SLIDE_OFFSET_PERCENT = 30
    
    fun fadeInTransition(durationMillis: Int = FADE_DURATION): EnterTransition {
        return fadeIn(
            animationSpec = tween(durationMillis),
            initialAlpha = 0f
        )
    }
    
    fun fadeOutTransition(durationMillis: Int = FADE_DURATION): ExitTransition {
        return fadeOut(
            animationSpec = tween(durationMillis),
            targetAlpha = 0f
        )
    }
    
    fun slideInFromRightTransition(durationMillis: Int = DEFAULT_DURATION): EnterTransition {
        return slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth * SLIDE_OFFSET_PERCENT / 100 },
            animationSpec = tween(durationMillis)
        ) + fadeIn(
            animationSpec = tween(durationMillis),
            initialAlpha = 0f
        )
    }
    
    fun slideOutToLeftTransition(durationMillis: Int = DEFAULT_DURATION): ExitTransition {
        return slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth * SLIDE_OFFSET_PERCENT / 100 },
            animationSpec = tween(durationMillis)
        ) + fadeOut(
            animationSpec = tween(durationMillis),
            targetAlpha = 0f
        )
    }
    
    fun slideInFromLeftTransition(durationMillis: Int = DEFAULT_DURATION): EnterTransition {
        return slideInHorizontally(
            initialOffsetX = { fullWidth -> -fullWidth * SLIDE_OFFSET_PERCENT / 100 },
            animationSpec = tween(durationMillis)
        ) + fadeIn(
            animationSpec = tween(durationMillis),
            initialAlpha = 0f
        )
    }
    
    fun slideOutToRightTransition(durationMillis: Int = DEFAULT_DURATION): ExitTransition {
        return slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth * SLIDE_OFFSET_PERCENT / 100 },
            animationSpec = tween(durationMillis)
        ) + fadeOut(
            animationSpec = tween(durationMillis),
            targetAlpha = 0f
        )
    }
}
