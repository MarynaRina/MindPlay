package com.mind.play.data.repository

import com.mind.play.data.database.dao.GameResultDao
import com.mind.play.data.database.dao.ProgressDao
import com.mind.play.data.database.entities.DailyProgressEntity
import com.mind.play.data.database.entities.GameResultEntity
import com.mind.play.domain.models.DailyProgress
import com.mind.play.domain.models.GameResult
import com.mind.play.domain.models.WeeklyStats
import com.mind.play.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ProgressRepositoryImpl(
    private val progressDao: ProgressDao,
    private val gameResultDao: GameResultDao
) : ProgressRepository {
    
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    
    override fun getDailyProgress(date: LocalDate): Flow<DailyProgress> {
        val dateString = date.format(formatter)
        return progressDao.observeProgress(dateString).map { entity ->
            entity?.toDomain() ?: DailyProgress.default(date)
        }
    }
    
    override fun getTodayProgress(): Flow<DailyProgress> = getDailyProgress(LocalDate.now())
    
    override suspend fun incrementGamesPlayed() {
        val today = LocalDate.now()
        ensureProgressExists(today)
        progressDao.incrementGamesPlayed(today.format(formatter), 1)
    }
    
    override suspend fun addMinutesPlayed(minutes: Int) {
        val today = LocalDate.now()
        ensureProgressExists(today)
        progressDao.addMinutes(today.format(formatter), minutes)
    }
    
    override fun getWeeklyStats(weekOffset: Int): Flow<WeeklyStats> {
        val startOfWeek = currentWeekStart().minusWeeks(weekOffset.toLong())
        val endOfWeek = startOfWeek.plusDays(6)
        val startString = startOfWeek.format(formatter)
        val endString = endOfWeek.format(formatter)
        return progressDao.observeProgressBetween(startString, endString).map { entities ->
            val map = entities.associateBy { LocalDate.parse(it.date, formatter) }
            val minutes = mutableListOf<Int>()
            val targets = mutableListOf<Boolean>()
            for (i in 0 until 7) {
                val day = startOfWeek.plusDays(i.toLong())
                val entity = map[day]
                minutes += entity?.minutesPlayed ?: 0
                targets += (entity?.gamesPlayed ?: 0) >= (entity?.totalGames ?: DailyProgress.DEFAULT_DAILY_TARGET)
            }
            WeeklyStats(
                weekStartDate = startOfWeek,
                dailyMinutes = minutes,
                dailyTargetMet = targets
            )
        }
    }
    
    override fun getCurrentWeekStats(): Flow<WeeklyStats> = getWeeklyStats(0)
    
    override suspend fun saveGameResult(result: GameResult) {
        gameResultDao.insertResult(GameResultEntity.fromDomain(result))
    }
    
    override fun getGameResults(gameType: String): Flow<List<GameResult>> =
        gameResultDao.observeResultsForGame(gameType).map { list ->
            list.map { it.toDomain() }
        }
    
    override fun getAllGameResults(): Flow<List<GameResult>> =
        gameResultDao.observeAllResults().map { list -> list.map { it.toDomain() } }
    
    private suspend fun ensureProgressExists(date: LocalDate) {
        val dateString = date.format(formatter)
        val existing = progressDao.getProgress(dateString)
        if (existing == null) {
            progressDao.insertProgress(DailyProgressEntity.empty(date))
        }
    }
    
    private fun currentWeekStart(): LocalDate {
        val today = LocalDate.now()
        return today.minusDays((today.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong())
    }
}
