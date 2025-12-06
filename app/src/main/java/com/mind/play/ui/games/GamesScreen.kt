package com.mind.play.ui.games

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mind.play.R
import com.mind.play.core.components.AnimatedCard
import com.mind.play.ui.theme.MindPlayTheme

data class Game(
    val id: String,
    val name: String,
    val drawableRes: Int
)

private val games = listOf(
    Game("arytmetyka", "Arytmetyka", R.drawable.card_aritmetic),
    Game("memory", "Memory", R.drawable.card_memory),
    Game("pary", "Pary/Różnice", R.drawable.card_pairs),
    Game("puzzle", "Puzzle", R.drawable.card_puzzle),
    Game("uwaga", "Uwaga/Reakcja", R.drawable.card_reaction),
    Game("simon", "Simon", R.drawable.card_simon)
)

@Composable
fun GamesScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Text(
            text = "Gry",
            style = MaterialTheme.typography.displayLarge,
            color = MindPlayTheme.colors.textHeading
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Zagraj w grę",
            style = MaterialTheme.typography.bodyLarge,
            color = MindPlayTheme.colors.textSecondary,
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(games) { game ->
                GameCard(
                    game = game,
                    onClick = { }
                )
            }
        }
    }
}

@Composable
private fun GameCard(
    game: Game,
    onClick: () -> Unit
) {
    AnimatedCard(
        onClick = onClick,
        pressScale = 0.92f,
        animationDuration = 100
    ) { modifier ->
        Image(
            painter = painterResource(id = game.drawableRes),
            contentDescription = game.name,
            modifier = modifier.aspectRatio(1f)
        )
    }
}
