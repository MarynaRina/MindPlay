package com.mind.play.ui.dashboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.mind.play.ui.theme.ButtonPrimaryBackground
import com.mind.play.ui.theme.InterRegular
import com.mind.play.ui.theme.MindPlayTheme

@Composable
fun HomeScreen() {
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
    
    val currentGames = 3
    val totalGames = 5
    
    var currentPage by remember { mutableIntStateOf(0) }
    
    val week1Data = listOf(
        Triple("24 Lis", 25, true),
        Triple("25 Lis", 15, true),
        Triple("26 Lis", 10, false),
        Triple("27 Lis", 20, true),
        Triple("28 Lis", 28, true),
        Triple("29 Lis", 30, true),
        Triple("30 Lis", 5, false)
    )
    
    val week2Data = listOf(
        Triple("1 Gru", 18, true),
        Triple("2 Gru", 22, true),
        Triple("3 Gru", 12, false),
        Triple("4 Gru", 25, true),
        Triple("5 Gru", 30, true),
        Triple("6 Gru", 8, false),
        Triple("7 Gru", 15, true)
    )
    
    val currentWeekData = if (currentPage == 0) week1Data else week2Data
    
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
            color = MindPlayTheme.colors.textPrimary,
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Postęp dnia:",
            fontFamily = InterRegular,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            color = MindPlayTheme.colors.textSecondary,
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        CircularProgressBar(
            current = currentGames,
            total = totalGames,
            size = 180.dp,
            strokeWidth = 18.dp,
            animationProgress = animProgress.value
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Text(
            text = "Progres tygodnia:",
            fontFamily = InterRegular,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            color = MindPlayTheme.colors.textSecondary,
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(ButtonPrimaryBackground)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        if (dragAmount < -50 && currentPage == 0) {
                            currentPage = 1
                        } else if (dragAmount > 50 && currentPage == 1) {
                            currentPage = 0
                        }
                    }
                }
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier
                            .width(35.dp)
                            .height(140.dp),
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
                            .height(140.dp)
                    ) {
                        val barWidth = size.width / (currentWeekData.size * 1.5f)
                        val maxMinutes = 30f
                        val spacing = barWidth * 0.5f
                        val animValue = animProgress.value
                        
                        currentWeekData.forEachIndexed { index, (_, minutes, targetMet) ->
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
                    currentWeekData.forEach { (date, _, _) ->
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
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .width(if (currentPage == 0) 20.dp else 8.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (currentPage == 0) 
                            MindPlayTheme.colors.inactive 
                        else 
                            MindPlayTheme.colors.inactive.copy(alpha = 0.5f)
                    )
            )
            
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .width(if (currentPage == 1) 20.dp else 8.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (currentPage == 1) 
                            MindPlayTheme.colors.inactive 
                        else 
                            MindPlayTheme.colors.inactive.copy(alpha = 0.5f)
                    )
            )
        }
    }
}

