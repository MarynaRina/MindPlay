package com.mind.play.ui.games.puzzle.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mind.play.ui.theme.MindPlayTheme

@Composable
fun PuzzleBoard(
    tiles: List<Int>,
    gridSize: Int,
    onTileClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(gridSize),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        userScrollEnabled = false
    ) {
        itemsIndexed(tiles) { index, number ->
            PuzzleTile(
                number = number,
                onClick = { onTileClick(index) }
            )
        }
    }
}

@Composable
private fun PuzzleTile(
    number: Int,
    onClick: () -> Unit
) {
    if (number == 0) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .background(Color.Transparent)
        )
    } else {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(4.dp))
                .background(MindPlayTheme.colors.buttonPrimary)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.displaySmall,
                color = MindPlayTheme.colors.buttonPrimaryText,
                fontWeight = FontWeight.Bold
            )
        }
    }
}