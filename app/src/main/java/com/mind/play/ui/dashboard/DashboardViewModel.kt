package com.mind.play.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mind.play.domain.models.DailyProgress
import com.mind.play.domain.models.WeeklyStats
import com.mind.play.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class DashboardViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {
    
    private val sharing = SharingStarted.WhileSubscribed(5_000)
    
    val todayProgress = progressRepository
        .getTodayProgress()
        .stateIn(viewModelScope, sharing, DailyProgress.default())
    
    val currentWeekStats = progressRepository
        .getCurrentWeekStats()
        .stateIn(viewModelScope, sharing, WeeklyStats.empty())
    
    private val _weekOffset = MutableStateFlow(0)
    val weekOffset: StateFlow<Int> = _weekOffset.asStateFlow()
    
    val displayedWeekStats = _weekOffset
        .flatMapLatest { offset -> progressRepository.getWeeklyStats(offset) }
        .stateIn(viewModelScope, sharing, WeeklyStats.empty())
    
    fun nextWeek() {
        _weekOffset.update { it + 1 }
    }
    
    fun previousWeek() {
        _weekOffset.update { current ->
            if (current > 0) current - 1 else 0
        }
    }
}
