package com.mind.play.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mind.play.data.database.entities.DailyProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    
    @Query("SELECT * FROM daily_progress WHERE date = :date LIMIT 1")
    fun observeProgress(date: String): Flow<DailyProgressEntity?>
    
    @Query("SELECT * FROM daily_progress WHERE date = :date LIMIT 1")
    suspend fun getProgress(date: String): DailyProgressEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: DailyProgressEntity)
    
    @Query("UPDATE daily_progress SET gamesPlayed = gamesPlayed + :delta WHERE date = :date")
    suspend fun incrementGamesPlayed(date: String, delta: Int = 1)
    
    @Query("UPDATE daily_progress SET minutesPlayed = minutesPlayed + :minutes WHERE date = :date")
    suspend fun addMinutes(date: String, minutes: Int)
    
    @Query("SELECT * FROM daily_progress WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun observeProgressBetween(startDate: String, endDate: String): Flow<List<DailyProgressEntity>>
}
