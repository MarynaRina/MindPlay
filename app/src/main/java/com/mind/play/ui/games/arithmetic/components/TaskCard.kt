package com.mind.play.ui.games.arithmetic.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mind.play.domain.models.ArithmeticTask
import com.mind.play.ui.theme.ErrorRed
import com.mind.play.ui.theme.MindPlayTheme
import com.mind.play.ui.theme.SuccessGreen

@Composable
fun TaskCard(
    task: ArithmeticTask,
    selectedAnswer: Int?,
    isCorrect: Boolean?,
    onAnswerSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = task.question,
            style = MaterialTheme.typography.displaySmall,
            color = MindPlayTheme.colors.textHeading,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 48.dp)
        )
        
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                AnswerOption(
                    answer = task.options[0],
                    isSelected = selectedAnswer == task.options[0],
                    isCorrect = if (selectedAnswer == task.options[0]) isCorrect else null,
                    onClick = { onAnswerSelected(task.options[0]) },
                    enabled = selectedAnswer == null,
                    modifier = Modifier.weight(1f)
                )
                
                AnswerOption(
                    answer = task.options[1],
                    isSelected = selectedAnswer == task.options[1],
                    isCorrect = if (selectedAnswer == task.options[1]) isCorrect else null,
                    onClick = { onAnswerSelected(task.options[1]) },
                    enabled = selectedAnswer == null,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                AnswerOption(
                    answer = task.options[2],
                    isSelected = selectedAnswer == task.options[2],
                    isCorrect = if (selectedAnswer == task.options[2]) isCorrect else null,
                    onClick = { onAnswerSelected(task.options[2]) },
                    enabled = selectedAnswer == null,
                    modifier = Modifier.weight(1f)
                )
                
                AnswerOption(
                    answer = task.options[3],
                    isSelected = selectedAnswer == task.options[3],
                    isCorrect = if (selectedAnswer == task.options[3]) isCorrect else null,
                    onClick = { onAnswerSelected(task.options[3]) },
                    enabled = selectedAnswer == null,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun AnswerOption(
    answer: Int,
    isSelected: Boolean,
    isCorrect: Boolean?,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected && isCorrect == true -> SuccessGreen
            isSelected && isCorrect == false -> ErrorRed
            else -> Color.White
        },
        label = "answer_background"
    )
    
    val textColor = when {
        isSelected && (isCorrect == true || isCorrect == false) -> Color.White
        else -> MindPlayTheme.colors.textHeading
    }
    
    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(
                enabled = enabled,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick()
            },
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        shadowElevation = 4.dp,
        tonalElevation = 0.dp
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = answer.toString(),
                style = MaterialTheme.typography.displaySmall,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}
