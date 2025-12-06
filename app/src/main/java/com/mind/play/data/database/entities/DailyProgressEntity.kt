package com.mind.play.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mind.play.domain.models.DailyProgress
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Entity(tableName = "daily_progress")
data class DailyProgressEntity(
    @PrimaryKey val date: String,
    val gamesPlayed: Int,
    val totalGames: Int,
    val minutesPlayed: Int
) {
    fun toDomain(): DailyProgress = DailyProgress(
        date = LocalDate.parse(date, FORMATTER),
        gamesPlayed = gamesPlayed,
        totalGames = totalGames,
        minutesPlayed = minutesPlayed
    )
    
    companion object {
        private val FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE
        
        fun fromDomain(progress: DailyProgress): DailyProgressEntity =
            DailyProgressEntity(
                date = progress.date.format(FORMATTER),
                gamesPlayed = progress.gamesPlayed,
                totalGames = progress.totalGames,
                minutesPlayed = progress.minutesPlayed
            )
        
        fun empty(date: LocalDate, totalGames: Int = DailyProgress.DEFAULT_DAILY_TARGET): DailyProgressEntity =
            DailyProgressEntity(
                date = date.format(FORMATTER),
                gamesPlayed = 0,
                totalGames = totalGames,
                minutesPlayed = 0
            )
        
        fun LocalDate.toStorageString(): String = this.format(FORMATTER)
    }
}
