package com.mind.play.domain.models

import java.time.LocalDate

data class DailyProgress(
    val date: LocalDate,
    val gamesPlayed: Int,
    val totalGames: Int = DEFAULT_DAILY_TARGET,
    val minutesPlayed: Int
) {
    val progressFraction: Float
        get() = (gamesPlayed.toFloat() / totalGames.toFloat()).coerceIn(0f, 1f)
    
    companion object {
        const val DEFAULT_DAILY_TARGET = 5
        
        fun default(date: LocalDate = LocalDate.now()): DailyProgress =
            DailyProgress(
                date = date,
                gamesPlayed = 0,
                totalGames = DEFAULT_DAILY_TARGET,
                minutesPlayed = 0
            )
    }
}
