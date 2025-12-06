package com.mind.play.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mind.play.data.database.entities.GameResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameResultDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: GameResultEntity)
    
    @Query("SELECT * FROM game_results WHERE gameType = :gameType ORDER BY timestamp DESC")
    fun observeResultsForGame(gameType: String): Flow<List<GameResultEntity>>
    
    @Query("SELECT * FROM game_results ORDER BY timestamp DESC")
    fun observeAllResults(): Flow<List<GameResultEntity>>
}
