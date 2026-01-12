package com.mind.play.ui.games.uwaga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mind.play.data.repository.SettingsRepository
import com.mind.play.domain.models.GameResult
import com.mind.play.domain.repository.ProgressRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class UwagaGamePhase {
    WAITING_TO_START,
    PLAYING,
    GAME_OVER
}

data class UwagaGameState(
    val activeBlockIndex: Int? = null,
    val correctCount: Int = 0,
    val totalTasks: Int = 10,
    val phase: UwagaGamePhase = UwagaGamePhase.WAITING_TO_START,
    val isPaused: Boolean = false,
    val isFinished: Boolean = false,
    val stressMode: Boolean = false,
    val timeLeftSeconds: Int = 60,
    val gameStartTimeMillis: Long = 0L,
    val gameEndTimeMillis: Long = 0L,
    val lastTapWasWrong: Boolean = false,
    val wrongTapIndex: Int? = null
)

class UwagaViewModel(
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {
    
    private val _gameState = MutableStateFlow(UwagaGameState())
    val gameState: StateFlow<UwagaGameState> = _gameState.asStateFlow()
    
    private var activationJob: Job? = null
    private var timerJob: Job? = null
    private var gameStartTimeMillis: Long = 0L
    
    companion object {
        const val GRID_SIZE = 15 // 3x5 grid
    }
    
    fun startGame() {
        viewModelScope.launch {
            val settings = settingsRepository.settings.first()
            gameStartTimeMillis = System.currentTimeMillis()
            
            _gameState.value = UwagaGameState(
                phase = UwagaGamePhase.PLAYING,
                stressMode = settings?.stressMode ?: false,
                timeLeftSeconds = 60,
                gameStartTimeMillis = gameStartTimeMillis
            )
            
            // Timer only in stress mode (when stressMode toggle is OFF)
            if (!_gameState.value.stressMode) {
                startTimer()
            }
            
            activateRandomBlock()
        }
    }
    
    private fun activateRandomBlock() {
        activationJob?.cancel()
        activationJob = viewModelScope.launch {
            // Random delay before showing active block (500ms - 2000ms)
            val delayTime = Random.nextLong(500, 2000)
            delay(delayTime)
            
            if (_gameState.value.isPaused || _gameState.value.isFinished) return@launch
            
            val currentActive = _gameState.value.activeBlockIndex
            var newIndex: Int
            do {
                newIndex = Random.nextInt(GRID_SIZE)
            } while (newIndex == currentActive)
            
            _gameState.value = _gameState.value.copy(
                activeBlockIndex = newIndex,
                lastTapWasWrong = false,
                wrongTapIndex = null
            )
        }
    }
    
    fun onBlockTapped(index: Int) {
        if (_gameState.value.phase != UwagaGamePhase.PLAYING || 
            _gameState.value.isPaused ||
            _gameState.value.activeBlockIndex == null) {
            return
        }
        
        if (index == _gameState.value.activeBlockIndex) {
            // Correct tap
            val newCorrectCount = _gameState.value.correctCount + 1
            
            _gameState.value = _gameState.value.copy(
                correctCount = newCorrectCount,
                activeBlockIndex = null,
                lastTapWasWrong = false,
                wrongTapIndex = null
            )
            
            if (newCorrectCount >= _gameState.value.totalTasks) {
                finishGame(success = true)
            } else {
                activateRandomBlock()
            }
        } else {
            // Wrong tap
            _gameState.value = _gameState.value.copy(
                lastTapWasWrong = true,
                wrongTapIndex = index
            )
            
            // Clear wrong tap indicator after short delay
            viewModelScope.launch {
                delay(300)
                _gameState.value = _gameState.value.copy(
                    lastTapWasWrong = false,
                    wrongTapIndex = null
                )
            }
        }
    }
    
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_gameState.value.timeLeftSeconds > 0 && !_gameState.value.isFinished) {
                delay(1000)
                if (!_gameState.value.isPaused) {
                    _gameState.value = _gameState.value.copy(
                        timeLeftSeconds = _gameState.value.timeLeftSeconds - 1
                    )
                    
                    if (_gameState.value.timeLeftSeconds == 0) {
                        finishGame(success = false)
                    }
                }
            }
        }
    }
    
    fun pauseGame() {
        if (_gameState.value.phase == UwagaGamePhase.PLAYING) {
            _gameState.value = _gameState.value.copy(
                isPaused = true,
                activeBlockIndex = null // Hide active block during pause
            )
            activationJob?.cancel()
        }
    }
    
    fun resumeGame() {
        _gameState.value = _gameState.value.copy(isPaused = false)
        activateRandomBlock()
    }
    
    private fun finishGame(success: Boolean) {
        val endTime = System.currentTimeMillis()
        activationJob?.cancel()
        timerJob?.cancel()
        
        _gameState.value = _gameState.value.copy(
            phase = UwagaGamePhase.GAME_OVER,
            isFinished = true,
            gameEndTimeMillis = endTime,
            activeBlockIndex = null
        )
        
        viewModelScope.launch {
            val durationSeconds = ((endTime - gameStartTimeMillis) / 1000).toInt()
            
            progressRepository.saveGameResult(
                GameResult(
                    gameType = "uwaga",
                    score = _gameState.value.correctCount,
                    totalTasks = _gameState.value.totalTasks,
                    duration = durationSeconds,
                    timestamp = endTime,
                    stressMode = _gameState.value.stressMode
                )
            )
            progressRepository.incrementGamesPlayed()
            progressRepository.addMinutesPlayed(durationSeconds / 60)
        }
    }
    
    fun isGameSuccess(): Boolean {
        return _gameState.value.correctCount >= _gameState.value.totalTasks
    }
    
    fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return "$minutes:${secs.toString().padStart(2, '0')}"
    }
    
    fun getGameDuration(): Long {
        return if (_gameState.value.gameEndTimeMillis > 0) {
            _gameState.value.gameEndTimeMillis - _gameState.value.gameStartTimeMillis
        } else {
            System.currentTimeMillis() - _gameState.value.gameStartTimeMillis
        }
    }
    
    fun formatDuration(millis: Long): String {
        val totalSeconds = (millis / 1000).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return if (minutes > 0) {
            "$minutes ${if (minutes == 1) "minuta" else if (minutes in 2..4) "minuty" else "minut"} $seconds ${if (seconds == 1) "sekunda" else if (seconds in 2..4) "sekundy" else "sekund"}"
        } else {
            "$seconds ${if (seconds == 1) "sekunda" else if (seconds in 2..4) "sekundy" else "sekund"}"
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        activationJob?.cancel()
        timerJob?.cancel()
    }
}
