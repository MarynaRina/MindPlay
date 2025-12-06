package com.mind.play.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mind.play.data.database.dao.GameResultDao
import com.mind.play.data.database.dao.ProgressDao
import com.mind.play.data.database.entities.DailyProgressEntity
import com.mind.play.data.database.entities.GameResultEntity

@Database(
    entities = [DailyProgressEntity::class, GameResultEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun progressDao(): ProgressDao
    abstract fun gameResultDao(): GameResultDao
}
