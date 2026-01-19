package com.mind.play.ui.games.pary

import com.mind.play.R

enum class ParyGridMode(val columns: Int, val rows: Int) {
    GRID_2X4(columns = 2, rows = 4),
    GRID_3X4(columns = 3, rows = 4);

    val totalCards: Int get() = columns * rows
}

enum class ParyTaskType {
    FIND_DIFFERENT,

    FIND_PAIR
}

data class ParyCard(
    val id: Int,
    val iconType: ParyIconType,
    val isSelected: Boolean = false,
    val isCorrect: Boolean = false,
    val isWrong: Boolean = false
)

enum class ParyIconType(val iconRes: Int) {
    STAR(R.drawable.ic_memory_star),
    THUMBSUP(R.drawable.ic_memory_thumbsup),
    CLOVER(R.drawable.ic_memory_clover),
    RAINBOW(R.drawable.ic_memory_rainbow),
    DROP(R.drawable.ic_memory_drop),
    CHECK(R.drawable.ic_memory_check),
    SURFER(R.drawable.ic_memory_surfer),
    HUNDRED(R.drawable.ic_memory_hundred),
    BANG(R.drawable.ic_memory_bang),
    HEART(R.drawable.ic_memory_heart),
    FIRE(R.drawable.ic_memory_fire),
    LIGHTNING(R.drawable.ic_memory_lightning)
}

data class ParyGameState(
    val cards: List<ParyCard> = emptyList(),
    val currentRound: Int = 1,
    val totalRounds: Int = 10,

    val taskType: ParyTaskType = ParyTaskType.FIND_DIFFERENT,

    val gridMode: ParyGridMode = ParyGridMode.GRID_2X4,

    val differentCardIndex: Int? = null,

    val pairCardIndices: Pair<Int, Int>? = null,

    val firstSelectedIndex: Int? = null,

    val isProcessing: Boolean = false,
    val gamePhase: ParyGamePhase = ParyGamePhase.INTRO,

    val timeRemainingSeconds: Int = 60,
    val isStressMode: Boolean = false,

    val correctAnswers: Int = 0,
    val gameStartTimeMillis: Long = 0L,

    val isTimeUp: Boolean = false
)

enum class ParyGamePhase {
    INTRO,
    PLAYING,
    PAUSED,
    FINISHED
}
