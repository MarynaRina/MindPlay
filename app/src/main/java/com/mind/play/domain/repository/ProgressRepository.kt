package com.mind.play.domain.repository

import com.mind.play.domain.models.DailyProgress
import com.mind.play.domain.models.GameResult
import com.mind.play.domain.models.WeeklyStats
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ProgressRepository {
    fun getDailyProgress(date: LocalDate): Flow<DailyProgress>
    fun getTodayProgress(): Flow<DailyProgress>
    suspend fun incrementGamesPlayed()
    suspend fun addMinutesPlayed(minutes: Int)
    
    fun getWeeklyStats(weekOffset: Int = 0): Flow<WeeklyStats>
    fun getCurrentWeekStats(): Flow<WeeklyStats>
    
    suspend fun saveGameResult(result: GameResult)
    fun getGameResults(gameType: String): Flow<List<GameResult>>
    fun getAllGameResults(): Flow<List<GameResult>>
}
