package com.mind.play.ui.games.puzzle

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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs

class PuzzleViewModel(
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PuzzleGameState())
    val state: StateFlow<PuzzleGameState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var gameStartTimeMillis: Long = 0L

    fun startGame(gridSize: Int = 3) {
        viewModelScope.launch {
            val settings = settingsRepository.settings.first()
            val isStressFreeMode = settings?.stressMode ?: false
            gameStartTimeMillis = System.currentTimeMillis()

            _state.update {
                it.copy(
                    isPlaying = true,
                    isFinished = false,
                    isPaused = false,
                    isWin = false,
                    moves = 0,
                    gridSize = gridSize,
                    stressMode = isStressFreeMode,
                    timeLeftSeconds = if (isStressFreeMode) 0 else 120
                )
            }
            shuffleBoard()

            if (!isStressFreeMode) {
                startTimer()
            }
        }
    }

    fun restartGame() {
        shuffleBoard()
        gameStartTimeMillis = System.currentTimeMillis()

        _state.update {
            val isStressFreeMode = it.stressMode
            it.copy(
                moves = 0,
                isPaused = false,
                isFinished = false,
                isWin = false,
                timeLeftSeconds = if (isStressFreeMode) 0 else 120
            )
        }

        if (!_state.value.stressMode) {
            startTimer()
        }
    }

    private fun shuffleBoard() {
        val size = _state.value.gridSize
        val solvedState = List(size * size) { index ->
            if (index == size * size - 1) 0 else index + 1
        }.toMutableList()

        var emptyIndex = solvedState.indexOf(0)
        var lastMoveIndex = -1

        val shuffleMoves = if (size == 4) 200 else 100

        repeat(shuffleMoves) {
            val validMoves = getValidMoves(emptyIndex, size)
                .filter { it != lastMoveIndex }

            if (validMoves.isNotEmpty()) {
                val nextMove = validMoves.random()
                solvedState[emptyIndex] = solvedState[nextMove]
                solvedState[nextMove] = 0
                lastMoveIndex = emptyIndex
                emptyIndex = nextMove
            }
        }

        _state.update { it.copy(tiles = solvedState) }
    }

    private fun getValidMoves(emptyIndex: Int, size: Int): List<Int> {
        val moves = mutableListOf<Int>()
        val row = emptyIndex / size
        val col = emptyIndex % size

        if (row > 0) moves.add(emptyIndex - size)
        if (row < size - 1) moves.add(emptyIndex + size)
        if (col > 0) moves.add(emptyIndex - 1)
        if (col < size - 1) moves.add(emptyIndex + 1)

        return moves
    }

    fun onTileClick(index: Int) {
        val currentState = _state.value
        if (currentState.isFinished || currentState.isPaused) return

        val tiles = currentState.tiles.toMutableList()
        val emptyIndex = tiles.indexOf(0)
        val size = currentState.gridSize

        val row1 = index / size
        val col1 = index % size
        val row2 = emptyIndex / size
        val col2 = emptyIndex % size

        if (abs(row1 - row2) + abs(col1 - col2) == 1) {
            tiles[emptyIndex] = tiles[index]
            tiles[index] = 0

            _state.update {
                it.copy(
                    tiles = tiles,
                    moves = it.moves + 1
                )
            }
            checkWinCondition()
        }
    }

    private fun checkWinCondition() {
        if (_state.value.checkWin()) {
            finishGame(true)
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_state.value.timeLeftSeconds > 0 && !_state.value.isPaused && !_state.value.isFinished) {
                delay(1000)
                _state.update {
                    it.copy(timeLeftSeconds = it.timeLeftSeconds - 1)
                }
                if (_state.value.timeLeftSeconds == 0) {
                    finishGame(false)
                }
            }
        }
    }

    fun pauseGame() {
        _state.update { it.copy(isPaused = true) }
        timerJob?.cancel()
    }

    fun resumeGame() {
        _state.update { it.copy(isPaused = false) }
        if (!_state.value.stressMode) startTimer()
    }

    private fun finishGame(isWin: Boolean) {
        timerJob?.cancel()
        val duration = ((System.currentTimeMillis() - gameStartTimeMillis) / 1000).toInt()

        val difficultyBonus = if (_state.value.gridSize == 4) 500 else 0

        val calculatedScore = if (isWin) {
            (1000 + difficultyBonus - (_state.value.moves * 5) - (duration * 2)).coerceAtLeast(10)
        } else 0

        _state.update {
            it.copy(
                isFinished = true,
                isWin = isWin,
                score = calculatedScore
            )
        }

        if (isWin) {
            viewModelScope.launch {
                progressRepository.saveGameResult(
                    GameResult(
                        gameType = "puzzle",
                        score = calculatedScore,
                        totalTasks = 1,
                        duration = duration,
                        timestamp = System.currentTimeMillis(),
                        stressMode = _state.value.stressMode
                    )
                )
                progressRepository.incrementGamesPlayed()
                progressRepository.addMinutesPlayed(duration / 60)
            }
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