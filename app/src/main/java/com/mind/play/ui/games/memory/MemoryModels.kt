package com.mind.play.ui.games.memory

import com.mind.play.R

/**
 * Сітка гри Memory (вибирається перед стартом)
 */
enum class MemoryGridMode(val columns: Int, val rows: Int) {
    GRID_2X4(columns = 2, rows = 4),
    GRID_3X4(columns = 3, rows = 4);

    val totalCards: Int get() = columns * rows
    val pairs: Int get() = totalCards / 2
}

/**
 * Картка в грі Memory
 */
data class MemoryCard(
    val id: Int,
    val iconType: MemoryIconType,
    val isFlipped: Boolean = false,
    val isMatched: Boolean = false,
    // тимчасово підсвічуємо неправильну пару червоним
    val isMismatched: Boolean = false
)

/**
 * Іконки (емоції) для карток.
 * ВАЖЛИВО: ресурси ic_memory_* тепер "обгорнуті" xml bitmap-ами, що посилаються на png у стилі дизайну.
 */
enum class MemoryIconType(val iconRes: Int) {
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

/**
 * Стан гри Memory
 */
data class MemoryGameState(
    val cards: List<MemoryCard> = emptyList(),
    val currentRound: Int = 1,
    val totalRounds: Int = 10,

    // режим сітки (2x4 або 3x4)
    val gridMode: MemoryGridMode = MemoryGridMode.GRID_2X4,

    val matchedPairs: Int = 0,

    // Кількість пар у поточному режимі (для 2x4 = 4, для 3x4 = 6)
    val totalPairs: Int = MemoryGridMode.GRID_2X4.pairs,

    val firstSelectedCard: Int? = null,
    val secondSelectedCard: Int? = null,
    val isProcessing: Boolean = false,
    val gamePhase: MemoryGamePhase = MemoryGamePhase.INTRO,

    // timer (stress-mode)
    val timeRemainingSeconds: Int = 60,
    val isStressMode: Boolean = false,

    // статистика
    val correctAnswers: Int = 0,
    val gameStartTimeMillis: Long = 0L,

    // чи закінчився час (для екрана “Spróbuj jeszcze raz”)
    val isTimeUp: Boolean = false
)

/**
 * Фази гри
 */
enum class MemoryGamePhase {
    INTRO,
    PLAYING,
    PAUSED,
    ROUND_COMPLETE,
    FINISHED
}
