package com.mind.play.core.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

object NavigationAnimations {
    const val DEFAULT_DURATION = 300
    const val FADE_DURATION = 200
    
    fun fadeInTransition(durationMillis: Int = FADE_DURATION): EnterTransition {
        return fadeIn(animationSpec = tween(durationMillis))
    }
    
    fun fadeOutTransition(durationMillis: Int = FADE_DURATION): ExitTransition {
        return fadeOut(animationSpec = tween(durationMillis))
    }
    
    fun slideInFromRightTransition(durationMillis: Int = DEFAULT_DURATION): EnterTransition {
        return slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(durationMillis)
        ) + fadeIn(animationSpec = tween(durationMillis))
    }
    
    fun slideOutToLeftTransition(durationMillis: Int = DEFAULT_DURATION): ExitTransition {
        return slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(durationMillis)
        ) + fadeOut(animationSpec = tween(durationMillis))
    }
    
    fun slideInFromLeftTransition(durationMillis: Int = DEFAULT_DURATION): EnterTransition {
        return slideInHorizontally(
            initialOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(durationMillis)
        ) + fadeIn(animationSpec = tween(durationMillis))
    }
    
    fun slideOutToRightTransition(durationMillis: Int = DEFAULT_DURATION): ExitTransition {
        return slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(durationMillis)
        ) + fadeOut(animationSpec = tween(durationMillis))
    }
}
