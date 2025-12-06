package com.mind.play.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mind.play.core.components.MindPlayRadioButton
import com.mind.play.core.components.MindPlayToggle
import com.mind.play.ui.theme.MindPlayTheme
import com.mind.play.ui.theme.TextSize
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Text(
            text = "Ustawienia",
            style = MaterialTheme.typography.displayLarge,
            color = MindPlayTheme.colors.textHeading
        )
        Spacer(modifier = Modifier.height(24.dp))
        SettingCard(
            backgroundColor = Color.White.copy(alpha = 0.95f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kontrast i czytelność",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MindPlayTheme.colors.textSecondary,
                    modifier = Modifier.weight(1f)
                )
                MindPlayToggle(
                    checked = uiState.highContrast,
                    onCheckedChange = viewModel::onHighContrastToggled
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        SettingCard(
            backgroundColor = Color.White.copy(alpha = 0.95f)
        ) {
            Text(
                text = "Rozmiar tekstu",
                style = MaterialTheme.typography.bodyLarge,
                color = MindPlayTheme.colors.textSecondary,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TextSizeOption(
                    label = "Mały",
                    size = TextSize.SMALL,
                    selected = uiState.textSize,
                    onSelect = viewModel::onTextSizeSelected
                )
                TextSizeOption(
                    label = "Średni",
                    size = TextSize.MEDIUM,
                    selected = uiState.textSize,
                    onSelect = viewModel::onTextSizeSelected
                )
                TextSizeOption(
                    label = "Duży",
                    size = TextSize.LARGE,
                    selected = uiState.textSize,
                    onSelect = viewModel::onTextSizeSelected
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        SettingCard(
            backgroundColor = Color.White.copy(alpha = 0.95f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tryb bez stresu",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MindPlayTheme.colors.textSecondary,
                    modifier = Modifier.weight(1f)
                )
                MindPlayToggle(
                    checked = uiState.stressMode,
                    onCheckedChange = viewModel::onStressModeToggled
                )
            }
        }
    }
}

@Composable
private fun SettingCard(
    backgroundColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(28.dp))
            .background(color = backgroundColor, shape = RoundedCornerShape(28.dp))
            .padding(horizontal = 20.dp, vertical = 18.dp),
        content = content
    )
}

@Composable
private fun TextSizeOption(
    label: String,
    size: TextSize,
    selected: TextSize,
    onSelect: (TextSize) -> Unit
) {
    MindPlayRadioButton(
        label = label,
        selected = selected == size,
        onClick = { onSelect(size) }
    )
}
