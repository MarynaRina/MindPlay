package com.mind.play.domain.models

data class ArithmeticTask(
    val number1: Int,
    val number2: Int,
    val operation: Operation,
    val correctAnswer: Int,
    val options: List<Int>
) {
    val question: String
        get() = "$number1 ${operation.symbol} $number2"
}

enum class Operation(val symbol: String) {
    ADDITION("+"),
    SUBTRACTION("−"),
    MULTIPLICATION("×"),
    DIVISION("÷")
}

data class ArithmeticGameState(
    val currentTask: ArithmeticTask? = null,
    val currentTaskIndex: Int = 0,
    val totalTasks: Int = 10,
    val score: Int = 0,
    val selectedAnswer: Int? = null,
    val isCorrect: Boolean? = null,
    val isPlaying: Boolean = false,
    val isPaused: Boolean = false,
    val isFinished: Boolean = false,
    val stressMode: Boolean = false,
    val timeLeftSeconds: Int = 120, // 2 minutes for stress mode
    val startTimeMillis: Long = 0L
) {
    val progress: Float
        get() = currentTaskIndex.toFloat() / totalTasks.toFloat()
}

sealed class AnswerState {
    object None : AnswerState()
    data class Selected(val answer: Int, val isCorrect: Boolean) : AnswerState()
}
