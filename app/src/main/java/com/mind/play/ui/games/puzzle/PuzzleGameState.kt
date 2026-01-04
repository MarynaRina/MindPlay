package com.mind.play.ui.games.puzzle

data class PuzzleGameState(
    val tiles: List<Int> = List(9) { it + 1 }.map { if (it == 9) 0 else it },
    val gridSize: Int = 3,
    val isPlaying: Boolean = false,
    val isPaused: Boolean = false,
    val isFinished: Boolean = false,
    val moves: Int = 0,
    val score: Int = 0,
    val stressMode: Boolean = false,
    val timeLeftSeconds: Int = 120,
    val isWin: Boolean = false
) {
    fun checkWin(): Boolean {
        if (tiles.isEmpty()) return false
        val target = List(gridSize * gridSize) { index ->
            if (index == gridSize * gridSize - 1) 0 else index + 1
        }
        return tiles == target
    }
}