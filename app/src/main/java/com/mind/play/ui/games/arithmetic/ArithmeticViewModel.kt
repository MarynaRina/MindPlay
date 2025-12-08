package com.mind.play.ui.games.arithmetic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mind.play.data.repository.SettingsRepository
import com.mind.play.domain.models.ArithmeticGameState
import com.mind.play.domain.models.ArithmeticTask
import com.mind.play.domain.models.GameResult
import com.mind.play.domain.models.Operation
import com.mind.play.domain.repository.ProgressRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random

class ArithmeticViewModel(
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {
    
    private val _gameState = MutableStateFlow(ArithmeticGameState())
    val gameState: StateFlow<ArithmeticGameState> = _gameState.asStateFlow()
    
    private var timerJob: Job? = null
    private var gameStartTimeMillis: Long = 0L
    
    fun startGame() {
        viewModelScope.launch {
            val settings = settingsRepository.settings.first()
            gameStartTimeMillis = System.currentTimeMillis()
            
            _gameState.value = _gameState.value.copy(
                isPlaying = true,
                isFinished = false,
                isPaused = false,
                currentTaskIndex = 0,
                score = 0,
                timeLeftSeconds = 120,
                stressMode = settings?.stressMode ?: false
            )
            
            generateNewTask()
            
            if (!_gameState.value.stressMode) {
                startTimer()
            }
        }
    }
    
    private fun generateNewTask() {
        val operation = Operation.entries.random()
        val task = when (operation) {
            Operation.MULTIPLICATION -> generateMultiplicationTask()
            Operation.ADDITION -> generateAdditionTask()
            Operation.SUBTRACTION -> generateSubtractionTask()
            Operation.DIVISION -> generateDivisionTask()
        }
        
        _gameState.value = _gameState.value.copy(
            currentTask = task,
            selectedAnswer = null,
            isCorrect = null
        )
    }
    
    private fun generateMultiplicationTask(): ArithmeticTask {
        val num1 = Random.nextInt(2, 20)
        val num2 = Random.nextInt(2, 15)
        val correctAnswer = num1 * num2
        val options = generateOptions(correctAnswer)
        
        return ArithmeticTask(
            number1 = num1,
            number2 = num2,
            operation = Operation.MULTIPLICATION,
            correctAnswer = correctAnswer,
            options = options
        )
    }
    
    private fun generateAdditionTask(): ArithmeticTask {
        val num1 = Random.nextInt(10, 100)
        val num2 = Random.nextInt(10, 100)
        val correctAnswer = num1 + num2
        val options = generateOptions(correctAnswer)
        
        return ArithmeticTask(
            number1 = num1,
            number2 = num2,
            operation = Operation.ADDITION,
            correctAnswer = correctAnswer,
            options = options
        )
    }
    
    private fun generateSubtractionTask(): ArithmeticTask {
        val num1 = Random.nextInt(20, 100)
        val num2 = Random.nextInt(10, num1)
        val correctAnswer = num1 - num2
        val options = generateOptions(correctAnswer)
        
        return ArithmeticTask(
            number1 = num1,
            number2 = num2,
            operation = Operation.SUBTRACTION,
            correctAnswer = correctAnswer,
            options = options
        )
    }
    
    private fun generateDivisionTask(): ArithmeticTask {
        val num2 = Random.nextInt(2, 12)
        val correctAnswer = Random.nextInt(2, 15)
        val num1 = num2 * correctAnswer
        val options = generateOptions(correctAnswer)
        
        return ArithmeticTask(
            number1 = num1,
            number2 = num2,
            operation = Operation.DIVISION,
            correctAnswer = correctAnswer,
            options = options
        )
    }
    
    private fun generateOptions(correctAnswer: Int): List<Int> {
        val options = mutableSetOf(correctAnswer)
        
        while (options.size < 4) {
            val offset = Random.nextInt(-10, 11)
            if (offset != 0) {
                val wrongAnswer = (correctAnswer + offset).coerceAtLeast(0)
                options.add(wrongAnswer)
            }
        }
        
        return options.shuffled()
    }
    
    fun selectAnswer(answer: Int) {
        val currentTask = _gameState.value.currentTask ?: return
        val isCorrect = answer == currentTask.correctAnswer
        
        _gameState.value = _gameState.value.copy(
            selectedAnswer = answer,
            isCorrect = isCorrect,
            score = if (isCorrect) _gameState.value.score + 1 else _gameState.value.score
        )
        
        viewModelScope.launch {
            delay(1000)
            nextTask()
        }
    }
    
    private fun nextTask() {
        val newIndex = _gameState.value.currentTaskIndex + 1
        
        if (newIndex >= _gameState.value.totalTasks) {
            finishGame()
        } else {
            _gameState.value = _gameState.value.copy(
                currentTaskIndex = newIndex
            )
            generateNewTask()
        }
    }
    
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_gameState.value.timeLeftSeconds > 0 && !_gameState.value.isPaused) {
                delay(1000)
                _gameState.value = _gameState.value.copy(
                    timeLeftSeconds = _gameState.value.timeLeftSeconds - 1
                )
                
                if (_gameState.value.timeLeftSeconds == 0) {
                    finishGame()
                }
            }
        }
    }
    
    fun pauseGame() {
        _gameState.value = _gameState.value.copy(isPaused = true)
        timerJob?.cancel()
    }
    
    fun resumeGame() {
        _gameState.value = _gameState.value.copy(isPaused = false)
        if (!_gameState.value.stressMode) {
            startTimer()
        }
    }
    
    private fun finishGame() {
        timerJob?.cancel()
        val gameDurationSeconds = ((System.currentTimeMillis() - gameStartTimeMillis) / 1000).toInt()
        
        _gameState.value = _gameState.value.copy(
            isFinished = true,
            isPaused = false
        )
        
        viewModelScope.launch {
            val currentState = _gameState.value
            progressRepository.saveGameResult(
                GameResult(
                    gameType = "arytmetyka",
                    score = currentState.score,
                    totalTasks = currentState.totalTasks,
                    duration = gameDurationSeconds,
                    timestamp = System.currentTimeMillis(),
                    stressMode = currentState.stressMode
                )
            )
            progressRepository.incrementGamesPlayed()
            progressRepository.addMinutesPlayed(gameDurationSeconds / 60)
        }
    }
    
    fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%d:%02d", minutes, secs)
    }
    
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
