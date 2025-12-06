package com.mind.play.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mind.play.domain.models.GameResult

@Entity(tableName = "game_results")
data class GameResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val gameType: String,
    val score: Int,
    val totalTasks: Int,
    val duration: Int,
    val timestamp: Long,
    val stressMode: Boolean
) {
    fun toDomain(): GameResult = GameResult(
        id = id,
        gameType = gameType,
        score = score,
        totalTasks = totalTasks,
        duration = duration,
        timestamp = timestamp,
        stressMode = stressMode
    )
    
    companion object {
        fun fromDomain(result: GameResult): GameResultEntity =
            GameResultEntity(
                id = result.id,
                gameType = result.gameType,
                score = result.score,
                totalTasks = result.totalTasks,
                duration = result.duration,
                timestamp = result.timestamp,
                stressMode = result.stressMode
            )
    }
}
