package com.mind.play.ui.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mind.play.R
import com.mind.play.ui.theme.ButtonPrimaryBackground
import com.mind.play.ui.theme.RubikBold
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    settingsLoaded: Boolean,
    shouldShowOnboarding: Boolean,
    onNavigateToWelcome: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    var iconVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(settingsLoaded, shouldShowOnboarding) {
        if (settingsLoaded) {
            iconVisible = true
            delay(2000)
            if (shouldShowOnboarding) {
                onNavigateToWelcome()
            } else {
                onNavigateToHome()
            }
        }
    }
    
    val iconAlpha = androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (iconVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 800)
    )
    
    val iconScale = androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (iconVisible) 1f else 0.85f,
        animationSpec = tween(durationMillis = 800)
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ButtonPrimaryBackground),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "MindPlay Logo",
            modifier = Modifier
                .size(120.dp)
                .alpha(iconAlpha.value)
                .scale(iconScale.value),
            tint = androidx.compose.ui.graphics.Color.Unspecified
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        LoadingText()
    }
}

@Composable
private fun LoadingText() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    
    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    
    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    
    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )
    
    Row(
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = "Loading",
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = RubikBold,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            ),
            color = MaterialTheme.colorScheme.onPrimary
        )
        
        Text(
            text = ".",
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = RubikBold,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            ),
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.alpha(dot1Alpha)
        )
        
        Text(
            text = ".",
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = RubikBold,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            ),
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.alpha(dot2Alpha)
        )
        
        Text(
            text = ".",
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = RubikBold,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            ),
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.alpha(dot3Alpha)
        )
    }
}
