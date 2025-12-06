package com.mind.play.ui.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mind.play.core.components.CircularProgressBar
import com.mind.play.ui.theme.InterRegular
import com.mind.play.ui.theme.MindPlayTheme
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: DashboardViewModel = koinViewModel()
) {
    var hasAnimated by remember { mutableStateOf(false) }
    
    val animProgress = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        if (!hasAnimated) {
            animProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1000)
            )
            hasAnimated = true
        } else {
            animProgress.snapTo(1f)
        }
    }
    
    val todayProgress by viewModel.todayProgress.collectAsState()
    val weekStats by viewModel.displayedWeekStats.collectAsState()
    val currentWeekIndex by viewModel.weekOffset.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Aktywność",
            style = MaterialTheme.typography.displayLarge,
            color = MindPlayTheme.colors.textHeading,
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Postęp dnia:",
            style = MaterialTheme.typography.bodyLarge,
            color = MindPlayTheme.colors.textSecondary,
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        CircularProgressBar(
            current = todayProgress.gamesPlayed,
            total = todayProgress.totalGames,
            size = 180.dp,
            strokeWidth = 18.dp,
            animationProgress = animProgress.value
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Text(
            text = "Progres tygodnia:",
            style = MaterialTheme.typography.bodyLarge,
            color = MindPlayTheme.colors.textSecondary,
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MindPlayTheme.colors.buttonPrimary)
                .pointerInput(Unit) {
                    var accumulatedDrag = 0f
                    var handledDrag = false
                    detectHorizontalDragGestures(
                        onDragStart = {
                            accumulatedDrag = 0f
                            handledDrag = false
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            if (handledDrag) return@detectHorizontalDragGestures
                            accumulatedDrag += dragAmount
                            when {
                                accumulatedDrag <= -60f -> {
                                    viewModel.nextWeek()
                                    handledDrag = true
                                }
                                accumulatedDrag >= 60f -> {
                                    viewModel.previousWeek()
                                    handledDrag = true
                                }
                            }
                        }
                    )
                }
                .padding(16.dp)
        ) {
            AnimatedContent(
                targetState = Pair(currentWeekIndex, weekStats),
                transitionSpec = {
                    val direction = when {
                        targetState.first > initialState.first -> 1
                        targetState.first < initialState.first -> -1
                        else -> 0
                    }
                    if (direction == 0) {
                        (fadeIn(animationSpec = tween(200))) togetherWith
                            (fadeOut(animationSpec = tween(200)))
                    } else {
                        (slideInHorizontally(
                            animationSpec = tween(350)
                        ) { fullWidth -> direction * fullWidth } + fadeIn()) togetherWith
                            (slideOutHorizontally(
                                animationSpec = tween(350)
                            ) { fullWidth -> -direction * fullWidth } + fadeOut())
                    }
                },
                label = "weekChart"
            ) { (_, stats) ->
                val chartData = stats.dayLabels(Locale("pl", "PL"))
                    .zip(stats.dailyMinutes.zip(stats.dailyTargetMet)) { label, pair ->
                        Triple(label, pair.first, pair.second)
                    }
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier
                                .width(35.dp)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf("30m", "25m", "20m", "15m", "10m", "5m", "0m").forEach { label ->
                                Text(
                                    text = label,
                                    fontFamily = InterRegular,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Canvas(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            val barWidth = size.width / (chartData.size * 1.5f)
                            val maxMinutes = 30f
                            val spacing = barWidth * 0.5f
                            val animValue = animProgress.value
                            
                            chartData.forEachIndexed { index, (_, minutes, targetMet) ->
                                val targetHeight = (minutes / maxMinutes) * size.height
                                val barHeight = targetHeight * animValue
                                val x = index * (barWidth + spacing)
                                val y = size.height - barHeight
                                
                                drawRoundRect(
                                    color = if (targetMet) Color.White else Color.White.copy(alpha = 0.4f),
                                    topLeft = Offset(x, y),
                                    size = Size(barWidth, barHeight),
                                    cornerRadius = CornerRadius(8f, 8f)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 43.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        chartData.forEach { (date, _, _) ->
                            Text(
                                text = date,
                                fontFamily = InterRegular,
                                fontSize = 10.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .width(if (currentWeekIndex == 0) 20.dp else 8.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (currentWeekIndex == 0)
                            MindPlayTheme.colors.inactive
                        else
                            MindPlayTheme.colors.inactive.copy(alpha = 0.5f)
                    )
            )
            
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .width(if (currentWeekIndex == 1) 20.dp else 8.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (currentWeekIndex == 1)
                            MindPlayTheme.colors.inactive
                        else
                            MindPlayTheme.colors.inactive.copy(alpha = 0.5f)
                    )
            )
        }
    }
}
