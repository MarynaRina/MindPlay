package com.mind.play.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mind.play.core.components.MindPlayRadioButton
import com.mind.play.core.components.MindPlayToggle
import com.mind.play.core.components.PrimaryButton
import com.mind.play.ui.theme.MindPlayTheme
import com.mind.play.ui.theme.TextSize
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState.isCompleted) {
        if (uiState.isCompleted) {
            onFinished()
        }
    }
    
    MindPlayTheme(
        highContrast = uiState.highContrast,
        textSize = uiState.textSize
    ) {
        OnboardingContent(
            state = uiState,
            onHighContrastChanged = viewModel::onHighContrastChanged,
            onTextSizeSelected = viewModel::onTextSizeSelected,
            onStressModeChanged = viewModel::onStressModeChanged,
            onPrimaryClick = viewModel::onPrimaryAction
        )
    }
}

@Composable
private fun OnboardingContent(
    state: OnboardingUiState,
    onHighContrastChanged: (Boolean) -> Unit,
    onTextSizeSelected: (TextSize) -> Unit,
    onStressModeChanged: (Boolean) -> Unit,
    onPrimaryClick: () -> Unit
) {
    val step = state.currentStep
    val buttonLabel = if (step == OnboardingStep.Summary) "GOTOWE" else "DALEJ"
    val (title, description) = stepText(step)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .height(460.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.displayLarge,
                color = MindPlayTheme.colors.textHeading
            )
            
        Spacer(modifier = Modifier.height(16.dp))
        
        val bodyText = if (step == OnboardingStep.Summary) {
            "Możesz zaczynać codzienny trening umysłowy. Życzymy miłej zabawy i dobrego samopoczucia!"
        } else {
            description
        }
        
        Text(
            text = bodyText,
            style = MaterialTheme.typography.bodyLarge,
            color = MindPlayTheme.colors.textSecondary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
            
            when (step) {
                OnboardingStep.Contrast -> ContrastStep(
                    checked = state.highContrast,
                    onCheckedChange = onHighContrastChanged
                )
                
                OnboardingStep.TextSize -> TextSizeStep(
                    selected = state.textSize,
                    onSelect = onTextSizeSelected
                )
                
                OnboardingStep.StressFree -> StressFreeStep(
                    enabled = state.stressMode,
                    onCheckedChange = onStressModeChanged
                )
                
                OnboardingStep.Summary -> {}
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            PrimaryButton(
                text = buttonLabel,
                onClick = onPrimaryClick
            )
        }
    }
}

@Composable
private fun ContrastStep(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MindPlayToggle(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = if (checked) "Włączony" else "Wyłączony",
            style = MaterialTheme.typography.bodyLarge,
            color = MindPlayTheme.colors.textSecondary
        )
    }
}

@Composable
private fun TextSizeStep(
    selected: TextSize,
    onSelect: (TextSize) -> Unit
) {
    val options = listOf(
        TextSize.SMALL to "Mały",
        TextSize.MEDIUM to "Średni",
        TextSize.LARGE to "Duży"
    )
    
    Column {
        options.forEach { (size, label) ->
            MindPlayRadioButton(
                label = label,
                selected = selected == size,
                onClick = { onSelect(size) }
            )
        }
    }
}

@Composable
private fun StressFreeStep(
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MindPlayToggle(
            checked = enabled,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = if (enabled) "Włączony" else "Wyłączony",
            style = MaterialTheme.typography.bodyLarge,
            color = MindPlayTheme.colors.textSecondary
        )
    }
}

private fun stepText(step: OnboardingStep): Pair<String, String> = when (step) {
    OnboardingStep.Contrast -> "Kontrast i czytelność" to
        "Możesz włączyć tryb wysokiego kontrastu, jeśli potrzebujesz wyraźniejszych kolorów i lepszej widoczności elementów."
    OnboardingStep.TextSize -> "Rozmiar tekstu" to
        "Dopasuj rozmiar liter, aby wygodnie korzystać z aplikacji."
    OnboardingStep.StressFree -> "Tryb bez stresu" to
        "W tym trybie nie ma limitu czasu ani kar. Możesz grać spokojnie i w swoim tempie."
    OnboardingStep.Summary -> "Wszystko ustawione!" to
        "Możesz zaczynać codzienny trening umysłowy. Życzymy miłej zabawy i dobrego samopoczucia!"
}
