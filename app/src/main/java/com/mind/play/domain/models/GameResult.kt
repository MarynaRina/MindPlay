package com.mind.play.domain.models

data class GameResult(
    val id: Long = 0,
    val gameType: String,
    val score: Int,
    val totalTasks: Int,
    val duration: Int,
    val timestamp: Long,
    val stressMode: Boolean
)
