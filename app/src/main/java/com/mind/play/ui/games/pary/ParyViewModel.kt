package com.mind.play.ui.games.pary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mind.play.core.sound.SoundManager
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

class ParyViewModel(
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ProgressRepository,
    private val soundManager: SoundManager
) : ViewModel() {

    private val _gameState = MutableStateFlow(ParyGameState())
    val gameState: StateFlow<ParyGameState> = _gameState.asStateFlow()

    private var timerJob: Job? = null

    private companion object {
        private const val START_TIME_SECONDS = 60

        private const val CORRECT_ANSWER_DELAY_MS = 800L

        private const val WRONG_ANSWER_DELAY_MS = 600L
    }

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                settings?.let {
                    _gameState.update { state ->
                        state.copy(isStressMode = !it.stressMode)
                    }
                }
            }
        }
    }

    fun setGridMode(mode: ParyGridMode) {
        val state = _gameState.value
        if (state.gamePhase != ParyGamePhase.INTRO) return

        _gameState.update {
            it.copy(gridMode = mode)
        }
    }

    fun startGame() {
        val newCards = generateCards()
        _gameState.update { state ->
            state.copy(
                cards = newCards.first,
                currentRound = 1,
                taskType = newCards.second,
                differentCardIndex = newCards.third,
                pairCardIndices = newCards.fourth,
                firstSelectedIndex = null,
                isProcessing = false,
                gamePhase = ParyGamePhase.PLAYING,
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

    private data class GeneratedCards(
        val cards: List<ParyCard>,
        val taskType: ParyTaskType,
        val differentCardIndex: Int?,
        val pairCardIndices: Pair<Int, Int>?
    )

    private fun generateCards(): Quadruple<List<ParyCard>, ParyTaskType, Int?, Pair<Int, Int>?> {
        val state = _gameState.value
        val totalCards = state.gridMode.totalCards

        val taskType = ParyTaskType.entries.random()
        
        return when (taskType) {
            ParyTaskType.FIND_DIFFERENT -> generateFindDifferentCards(totalCards)
            ParyTaskType.FIND_PAIR -> generateFindPairCards(totalCards)
        }
    }

    private fun generateFindDifferentCards(totalCards: Int): Quadruple<List<ParyCard>, ParyTaskType, Int?, Pair<Int, Int>?> {
        val icons = ParyIconType.entries.shuffled()
        val mainIcon = icons[0]
        val differentIcon = icons[1]

        val differentIndex = (0 until totalCards).random()
        
        val cards = mutableListOf<ParyCard>()
        for (i in 0 until totalCards) {
            cards.add(
                ParyCard(
                    id = i,
                    iconType = if (i == differentIndex) differentIcon else mainIcon
                )
            )
        }
        
        return Quadruple(cards, ParyTaskType.FIND_DIFFERENT, differentIndex, null)
    }

    private fun generateFindPairCards(totalCards: Int): Quadruple<List<ParyCard>, ParyTaskType, Int?, Pair<Int, Int>?> {
        val icons = ParyIconType.entries.shuffled()

        val allPositions = (0 until totalCards).shuffled()
        val pairPosition1 = allPositions[0]
        val pairPosition2 = allPositions[1]

        val pairIcon = icons[0]

        val otherIcons = icons.drop(1).take(totalCards - 2)
        
        val cards = mutableListOf<ParyCard>()
        var otherIconIndex = 0
        
        for (i in 0 until totalCards) {
            val icon = when (i) {
                pairPosition1, pairPosition2 -> pairIcon
                else -> {
                    val ic = otherIcons.getOrElse(otherIconIndex) { icons.random() }
                    otherIconIndex++
                    ic
                }
            }
            cards.add(ParyCard(id = i, iconType = icon))
        }
        
        return Quadruple(
            cards, 
            ParyTaskType.FIND_PAIR, 
            null, 
            Pair(minOf(pairPosition1, pairPosition2), maxOf(pairPosition1, pairPosition2))
        )
    }

    fun onCardClick(cardIndex: Int) {
        val state = _gameState.value

        if (state.isProcessing ||
            state.gamePhase != ParyGamePhase.PLAYING ||
            state.cards[cardIndex].isCorrect ||
            state.cards[cardIndex].isWrong
        ) {
            return
        }

        when (state.taskType) {
            ParyTaskType.FIND_DIFFERENT -> handleFindDifferentClick(cardIndex)
            ParyTaskType.FIND_PAIR -> handleFindPairClick(cardIndex)
        }
    }

    private fun handleFindDifferentClick(cardIndex: Int) {
        val state = _gameState.value
        val isCorrect = cardIndex == state.differentCardIndex
        
        soundManager.playTap()

        if (isCorrect) {
            handleCorrectAnswer(cardIndex)
        } else {
            handleWrongAnswer(cardIndex)
        }
    }

    private fun handleFindPairClick(cardIndex: Int) {
        val state = _gameState.value
        
        if (state.firstSelectedIndex == null) {
            soundManager.playTap()
            
            _gameState.update { s ->
                s.copy(
                    firstSelectedIndex = cardIndex,
                    cards = s.cards.mapIndexed { index, card ->
                        if (index == cardIndex) card.copy(isSelected = true) else card
                    }
                )
            }
        } else {
            val firstIndex = state.firstSelectedIndex
            val pairIndices = state.pairCardIndices

            val isCorrectPair = pairIndices != null &&
                ((firstIndex == pairIndices.first && cardIndex == pairIndices.second) ||
                 (firstIndex == pairIndices.second && cardIndex == pairIndices.first))

            val firstCard = state.cards[firstIndex]
            val secondCard = state.cards[cardIndex]
            val sameIcon = firstCard.iconType == secondCard.iconType && firstIndex != cardIndex
            
            if (isCorrectPair || sameIcon) {
                handleCorrectPairAnswer(firstIndex, cardIndex)
            } else {
                handleWrongPairAnswer(firstIndex, cardIndex)
            }
        }
    }

    private fun handleCorrectAnswer(cardIndex: Int) {
        soundManager.playCorrect()
        _gameState.update { s ->
            s.copy(
                isProcessing = true,
                cards = s.cards.mapIndexed { index, card ->
                    if (index == cardIndex) card.copy(isCorrect = true) else card
                }
            )
        }

        viewModelScope.launch {
            delay(CORRECT_ANSWER_DELAY_MS)
            proceedToNextRound()
        }
    }

    private fun handleCorrectPairAnswer(firstIndex: Int, secondIndex: Int) {
        soundManager.playCorrect()
        _gameState.update { s ->
            s.copy(
                isProcessing = true,
                cards = s.cards.mapIndexed { index, card ->
                    if (index == firstIndex || index == secondIndex) {
                        card.copy(isSelected = false, isCorrect = true)
                    } else card
                }
            )
        }

        viewModelScope.launch {
            delay(CORRECT_ANSWER_DELAY_MS)
            proceedToNextRound()
        }
    }

    private fun handleWrongAnswer(cardIndex: Int) {
        soundManager.playWrong()
        _gameState.update { s ->
            s.copy(
                isProcessing = true,
                cards = s.cards.mapIndexed { index, card ->
                    if (index == cardIndex) card.copy(isWrong = true) else card
                }
            )
        }

        viewModelScope.launch {
            delay(WRONG_ANSWER_DELAY_MS)
            _gameState.update { s ->
                s.copy(
                    isProcessing = false,
                    cards = s.cards.mapIndexed { index, card ->
                        if (index == cardIndex) card.copy(isWrong = false) else card
                    }
                )
            }
        }
    }

    private fun handleWrongPairAnswer(firstIndex: Int, secondIndex: Int) {
        soundManager.playWrong()
        _gameState.update { s ->
            s.copy(
                isProcessing = true,
                cards = s.cards.mapIndexed { index, card ->
                    when (index) {
                        firstIndex -> card.copy(isSelected = false, isWrong = true)
                        secondIndex -> card.copy(isWrong = true)
                        else -> card
                    }
                }
            )
        }

        viewModelScope.launch {
            delay(WRONG_ANSWER_DELAY_MS)
            _gameState.update { s ->
                s.copy(
                    isProcessing = false,
                    firstSelectedIndex = null,
                    cards = s.cards.map { card -> card.copy(isWrong = false, isSelected = false) }
                )
            }
        }
    }

    private fun proceedToNextRound() {
        val state = _gameState.value
        val newCorrectAnswers = state.correctAnswers + 1
        
        if (state.currentRound >= state.totalRounds) {
            timerJob?.cancel()
            _gameState.update { s ->
                s.copy(
                    correctAnswers = newCorrectAnswers,
                    gamePhase = ParyGamePhase.FINISHED,
                    isProcessing = false
                )
            }
        } else {
            val newCards = generateCards()
            _gameState.update { s ->
                s.copy(
                    correctAnswers = newCorrectAnswers,
                    cards = newCards.first,
                    currentRound = s.currentRound + 1,
                    taskType = newCards.second,
                    differentCardIndex = newCards.third,
                    pairCardIndices = newCards.fourth,
                    firstSelectedIndex = null,
                    isProcessing = false,
                    gamePhase = ParyGamePhase.PLAYING
                )
            }
        }
    }

    fun nextRound() {
        val newCards = generateCards()

        _gameState.update { state ->
            state.copy(
                cards = newCards.first,
                currentRound = state.currentRound + 1,
                taskType = newCards.second,
                differentCardIndex = newCards.third,
                pairCardIndices = newCards.fourth,
                firstSelectedIndex = null,
                isProcessing = false,
                gamePhase = ParyGamePhase.PLAYING,
                timeRemainingSeconds = if (state.isStressMode) START_TIME_SECONDS else state.timeRemainingSeconds,
                isTimeUp = false
            )
        }

        if (_gameState.value.isStressMode) {
            startTimer()
        }
    }

    fun togglePause() {
        val currentPhase = _gameState.value.gamePhase
        if (currentPhase == ParyGamePhase.PLAYING) {
            _gameState.update { it.copy(gamePhase = ParyGamePhase.PAUSED) }
            timerJob?.cancel()
        } else if (currentPhase == ParyGamePhase.PAUSED) {
            _gameState.update { it.copy(gamePhase = ParyGamePhase.PLAYING) }
            if (_gameState.value.isStressMode) startTimer()
        }
    }

    fun resumeGame() {
        _gameState.update { it.copy(gamePhase = ParyGamePhase.PLAYING) }
        if (_gameState.value.isStressMode) startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_gameState.value.timeRemainingSeconds > 0 &&
                _gameState.value.gamePhase == ParyGamePhase.PLAYING
            ) {
                delay(1000)
                _gameState.update { state ->
                    val newTime = state.timeRemainingSeconds - 1
                    if (newTime <= 0) {
                        state.copy(
                            timeRemainingSeconds = 0,
                            gamePhase = ParyGamePhase.FINISHED,
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
                    gameType = "pary",
                    score = state.correctAnswers,
                    totalTasks = state.totalRounds,
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
        return state.gamePhase == ParyGamePhase.FINISHED && !state.isTimeUp
    }

    fun restartGame() {
        timerJob?.cancel()
        _gameState.update { current ->
            ParyGameState(
                isStressMode = current.isStressMode,
                gridMode = current.gridMode
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
