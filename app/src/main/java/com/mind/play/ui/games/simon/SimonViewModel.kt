package com.mind.play.ui.games.simon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mind.play.core.sound.SoundManager
import com.mind.play.domain.models.GameResult
import com.mind.play.domain.repository.ProgressRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class SimonColor {
    GREEN, ORANGE, PINK, YELLOW
}

enum class SimonGamePhase {
    WAITING_TO_START,
    SHOWING_SEQUENCE,
    PLAYER_INPUT,
    GAME_OVER
}

data class SimonGameState(
    val sequence: List<SimonColor> = emptyList(),
    val playerInput: List<SimonColor> = emptyList(),
    val currentRound: Int = 0,
    val highlightedColor: SimonColor? = null,
    val wrongColor: SimonColor? = null,
    val phase: SimonGamePhase = SimonGamePhase.WAITING_TO_START,
    val isPaused: Boolean = false,
    val isFinished: Boolean = false,
    val gameStartTimeMillis: Long = 0L,
    val gameEndTimeMillis: Long = 0L
)

class SimonViewModel(
    private val progressRepository: ProgressRepository,
    private val soundManager: SoundManager
) : ViewModel() {
    
    private val _gameState = MutableStateFlow(SimonGameState())
    val gameState: StateFlow<SimonGameState> = _gameState.asStateFlow()
    
    private var sequenceJob: Job? = null
    private var gameStartTimeMillis: Long = 0L
    
    fun startGame() {
        gameStartTimeMillis = System.currentTimeMillis()
        _gameState.value = SimonGameState(
            phase = SimonGamePhase.WAITING_TO_START,
            gameStartTimeMillis = gameStartTimeMillis
        )
        startNewRound()
    }
    
    private fun startNewRound() {
        val currentSequence = _gameState.value.sequence.toMutableList()
        val newColor = SimonColor.entries[Random.nextInt(4)]
        currentSequence.add(newColor)
        
        _gameState.value = _gameState.value.copy(
            sequence = currentSequence,
            playerInput = emptyList(),
            currentRound = currentSequence.size,
            phase = SimonGamePhase.SHOWING_SEQUENCE
        )
        
        showSequence()
    }
    
    private fun showSequence() {
        sequenceJob?.cancel()
        sequenceJob = viewModelScope.launch {
            delay(500)
            
            for (color in _gameState.value.sequence) {
                if (_gameState.value.isPaused) {
                    while (_gameState.value.isPaused) {
                        delay(100)
                    }
                }
                
                _gameState.value = _gameState.value.copy(highlightedColor = color)
                playColorSound(color)
                delay(600)
                _gameState.value = _gameState.value.copy(highlightedColor = null)
                delay(300)
            }
            
            _gameState.value = _gameState.value.copy(
                phase = SimonGamePhase.PLAYER_INPUT
            )
        }
    }
    
    fun onColorPressed(color: SimonColor) {
        if (_gameState.value.phase != SimonGamePhase.PLAYER_INPUT || _gameState.value.isPaused) {
            return
        }
        
        val currentInput = _gameState.value.playerInput.toMutableList()
        val expectedColor = _gameState.value.sequence.getOrNull(currentInput.size)
        
        if (color == expectedColor) {
            currentInput.add(color)
            _gameState.value = _gameState.value.copy(
                playerInput = currentInput,
                highlightedColor = color
            )
            
            playColorSound(color)
            
            viewModelScope.launch {
                delay(200)
                _gameState.value = _gameState.value.copy(highlightedColor = null)
                
                if (currentInput.size == _gameState.value.sequence.size) {
                    delay(500)
                    startNewRound()
                }
            }
        } else {
            _gameState.value = _gameState.value.copy(
                wrongColor = color
            )
            
            soundManager.playWrong()
            
            viewModelScope.launch {
                delay(600)
                _gameState.value = _gameState.value.copy(wrongColor = null)
                delay(300)
                finishGame()
            }
        }
    }
    
    fun pauseGame() {
        if (_gameState.value.phase != SimonGamePhase.GAME_OVER) {
            _gameState.value = _gameState.value.copy(isPaused = true)
        }
    }
    
    fun resumeGame() {
        _gameState.value = _gameState.value.copy(isPaused = false)
        
        if (_gameState.value.phase == SimonGamePhase.SHOWING_SEQUENCE) {
            showSequence()
        }
    }
    
    private fun finishGame() {
        val endTime = System.currentTimeMillis()
        val completedRounds = _gameState.value.currentRound - 1
        
        _gameState.value = _gameState.value.copy(
            phase = SimonGamePhase.GAME_OVER,
            isFinished = true,
            gameEndTimeMillis = endTime
        )
        
        viewModelScope.launch {
            val durationSeconds = ((endTime - gameStartTimeMillis) / 1000).toInt()
            
            progressRepository.saveGameResult(
                GameResult(
                    gameType = "simon",
                    score = completedRounds,
                    totalTasks = completedRounds,
                    duration = durationSeconds,
                    timestamp = endTime,
                    stressMode = false
                )
            )
            progressRepository.incrementGamesPlayed()
            progressRepository.addMinutesPlayed(durationSeconds / 60)
        }
    }
    
    fun getCompletedRounds(): Int {
        return maxOf(0, _gameState.value.currentRound - 1)
    }
    
    fun formatTime(millis: Long): String {
        val totalSeconds = (millis / 1000).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return if (minutes > 0) {
            "$minutes ${if (minutes == 1) "minuta" else if (minutes in 2..4) "minuty" else "minut"} $seconds ${if (seconds == 1) "sekunda" else if (seconds in 2..4) "sekundy" else "sekund"}"
        } else {
            "$seconds ${if (seconds == 1) "sekunda" else if (seconds in 2..4) "sekundy" else "sekund"}"
        }
    }
    
    fun getGameDuration(): Long {
        return if (_gameState.value.gameEndTimeMillis > 0) {
            _gameState.value.gameEndTimeMillis - _gameState.value.gameStartTimeMillis
        } else {
            System.currentTimeMillis() - _gameState.value.gameStartTimeMillis
        }
    }
    
    private fun playColorSound(color: SimonColor) {
        val tone = when (color) {
            SimonColor.GREEN -> SoundManager.SimonTone.GREEN
            SimonColor.ORANGE -> SoundManager.SimonTone.ORANGE
            SimonColor.PINK -> SoundManager.SimonTone.PINK
            SimonColor.YELLOW -> SoundManager.SimonTone.YELLOW
        }
        soundManager.playSimonTone(tone)
    }
    
    override fun onCleared() {
        super.onCleared()
        sequenceJob?.cancel()
    }
}
