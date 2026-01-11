package com.mind.play.ui.games.memory

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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MemoryViewModel(
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _gameState = MutableStateFlow(MemoryGameState())
    val gameState: StateFlow<MemoryGameState> = _gameState.asStateFlow()

    private var timerJob: Job? = null

    private companion object {
        private const val START_TIME_SECONDS = 60

        // Підсвітка неправильного вибору: спочатку даємо карткам "дорозкритись"
        private const val REVEAL_BEFORE_HIGHLIGHT_MS = 220L
        // Тримаємо червону підсвітку так, щоб разом було ~0.8 сек
        private const val MISMATCH_HIGHLIGHT_MS = 580L

        private const val MATCH_FEEDBACK_DELAY_MS = 150L
    }

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                settings?.let {
                    _gameState.update { state ->
                        // IMPORTANT: логіка режимів поміняна місцями (як ти просив)
                        // Було: isStressMode = it.stressMode
                        // Стало: isStressMode = !it.stressMode
                        state.copy(isStressMode = !it.stressMode)
                    }
                }
            }
        }
    }

    /**
     * Вибір сітки ДО старту гри (2x4 / 3x4)
     */
    fun setGridMode(mode: MemoryGridMode) {
        val state = _gameState.value
        if (state.gamePhase != MemoryGamePhase.INTRO) return

        _gameState.update {
            it.copy(
                gridMode = mode,
                totalPairs = mode.pairs
            )
        }
    }

    fun startGame() {
        val shuffledCards = generateShuffledCards()
        _gameState.update { state ->
            state.copy(
                cards = shuffledCards,
                currentRound = 1,
                matchedPairs = 0,
                firstSelectedCard = null,
                secondSelectedCard = null,
                isProcessing = false,
                gamePhase = MemoryGamePhase.PLAYING,
                timeRemainingSeconds = START_TIME_SECONDS,
                correctAnswers = 0,
                gameStartTimeMillis = System.currentTimeMillis(),
                isTimeUp = false
            )
        }

        if (_gameState.value.isStressMode) {
            startTimer()
        }
    }

    private fun generateShuffledCards(): List<MemoryCard> {
        val state = _gameState.value
        val selectedIcons = MemoryIconType.entries.shuffled().take(state.totalPairs)

        val cards = mutableListOf<MemoryCard>()
        var cardId = 0

        selectedIcons.forEach { iconType ->
            cards.add(MemoryCard(id = cardId++, iconType = iconType))
            cards.add(MemoryCard(id = cardId++, iconType = iconType))
        }

        return cards.shuffled()
    }

    fun onCardClick(cardIndex: Int) {
        val state = _gameState.value

        if (state.isProcessing ||
            state.gamePhase != MemoryGamePhase.PLAYING ||
            state.cards[cardIndex].isFlipped ||
            state.cards[cardIndex].isMatched ||
            state.secondSelectedCard != null
        ) {
            return
        }

        if (state.firstSelectedCard == null) {
            _gameState.update { s ->
                s.copy(
                    firstSelectedCard = cardIndex,
                    cards = s.cards.mapIndexed { index, card ->
                        if (index == cardIndex) card.copy(isFlipped = true) else card
                    }
                )
            }
        } else {
            _gameState.update { s ->
                s.copy(
                    secondSelectedCard = cardIndex,
                    isProcessing = true,
                    cards = s.cards.mapIndexed { index, card ->
                        if (index == cardIndex) card.copy(isFlipped = true) else card
                    }
                )
            }
            checkMatch()
        }
    }

    private fun checkMatch() {
        viewModelScope.launch {
            val state = _gameState.value
            val firstIndex = state.firstSelectedCard ?: return@launch
            val secondIndex = state.secondSelectedCard ?: return@launch

            val firstCard = state.cards[firstIndex]
            val secondCard = state.cards[secondIndex]

            if (firstCard.iconType == secondCard.iconType) {
                delay(MATCH_FEEDBACK_DELAY_MS)

                _gameState.update { s ->
                    val newMatchedPairs = s.matchedPairs + 1
                    val allMatched = newMatchedPairs >= s.totalPairs

                    s.copy(
                        cards = s.cards.mapIndexed { index, card ->
                            if (index == firstIndex || index == secondIndex) {
                                card.copy(isMatched = true, isMismatched = false)
                            } else card
                        },
                        matchedPairs = newMatchedPairs,
                        correctAnswers = s.correctAnswers + 1,
                        firstSelectedCard = null,
                        secondSelectedCard = null,
                        isProcessing = false,
                        gamePhase = if (allMatched) {
                            if (s.currentRound >= s.totalRounds) MemoryGamePhase.FINISHED
                            else MemoryGamePhase.ROUND_COMPLETE
                        } else s.gamePhase
                    )
                }

                // стоп таймеру між раундами/фініші (як і було)
                if (_gameState.value.gamePhase in listOf(MemoryGamePhase.ROUND_COMPLETE, MemoryGamePhase.FINISHED)) {
                    timerJob?.cancel()
                }
            } else {
                // Червону підсвітку включаємо з затримкою,
                // щоб спочатку карта встигла розкритися
                delay(REVEAL_BEFORE_HIGHLIGHT_MS)

                _gameState.update { s ->
                    s.copy(
                        cards = s.cards.mapIndexed { index, card ->
                            if (index == firstIndex || index == secondIndex) {
                                card.copy(isMismatched = true)
                            } else card
                        }
                    )
                }

                delay(MISMATCH_HIGHLIGHT_MS)

                _gameState.update { s ->
                    s.copy(
                        cards = s.cards.mapIndexed { index, card ->
                            if (index == firstIndex || index == secondIndex) {
                                card.copy(isFlipped = false, isMismatched = false)
                            } else card
                        },
                        firstSelectedCard = null,
                        secondSelectedCard = null,
                        isProcessing = false
                    )
                }
            }
        }
    }

    fun nextRound() {
        val shuffledCards = generateShuffledCards()

        _gameState.update { state ->
            state.copy(
                cards = shuffledCards,
                currentRound = state.currentRound + 1,
                matchedPairs = 0,
                firstSelectedCard = null,
                secondSelectedCard = null,
                isProcessing = false,
                gamePhase = MemoryGamePhase.PLAYING,

                // IMPORTANT: тепер таймер скидається КОЖЕН раунд (1 хвилина на раунд)
                timeRemainingSeconds = if (state.isStressMode) START_TIME_SECONDS else state.timeRemainingSeconds,

                // якщо програли по часу раніше — при новому раунді це неактуально
                isTimeUp = false
            )
        }

        if (_gameState.value.isStressMode) {
            startTimer()
        }
    }

    fun togglePause() {
        val currentPhase = _gameState.value.gamePhase
        if (currentPhase == MemoryGamePhase.PLAYING) {
            _gameState.update { it.copy(gamePhase = MemoryGamePhase.PAUSED) }
            timerJob?.cancel()
        } else if (currentPhase == MemoryGamePhase.PAUSED) {
            _gameState.update { it.copy(gamePhase = MemoryGamePhase.PLAYING) }
            if (_gameState.value.isStressMode) startTimer()
        }
    }

    fun resumeGame() {
        _gameState.update { it.copy(gamePhase = MemoryGamePhase.PLAYING) }
        if (_gameState.value.isStressMode) startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_gameState.value.timeRemainingSeconds > 0 &&
                _gameState.value.gamePhase == MemoryGamePhase.PLAYING
            ) {
                delay(1000)
                _gameState.update { state ->
                    val newTime = state.timeRemainingSeconds - 1
                    if (newTime <= 0) {
                        state.copy(
                            timeRemainingSeconds = 0,
                            gamePhase = MemoryGamePhase.FINISHED,
                            isTimeUp = true
                        )
                    } else {
                        state.copy(timeRemainingSeconds = newTime)
                    }
                }
            }
        }
    }

    fun saveGameResult() {
        viewModelScope.launch {
            val state = _gameState.value
            val duration = ((System.currentTimeMillis() - state.gameStartTimeMillis) / 1000).toInt()

            progressRepository.saveGameResult(
                GameResult(
                    gameType = "memory",
                    score = state.correctAnswers,
                    totalTasks = state.totalRounds * state.totalPairs,
                    duration = duration,
                    timestamp = System.currentTimeMillis(),
                    stressMode = state.isStressMode
                )
            )
            progressRepository.incrementGamesPlayed()
            progressRepository.addMinutesPlayed(duration / 60)
        }
    }

    fun getScore(): Int = _gameState.value.correctAnswers

    fun getGameDurationSeconds(): Int {
        val state = _gameState.value
        return ((System.currentTimeMillis() - state.gameStartTimeMillis) / 1000).toInt()
    }

    fun isGameSuccessful(): Boolean {
        val state = _gameState.value
        return state.gamePhase == MemoryGamePhase.FINISHED && !state.isTimeUp
    }

    fun restartGame() {
        timerJob?.cancel()
        _gameState.update { current ->
            // зберігаємо обраний режим сітки та режим таймера
            MemoryGameState(
                isStressMode = current.isStressMode,
                gridMode = current.gridMode,
                totalPairs = current.totalPairs
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
