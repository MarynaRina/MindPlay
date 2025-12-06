package com.mind.play.domain.models

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class WeeklyStats(
    val weekStartDate: LocalDate,
    val dailyMinutes: List<Int>,
    val dailyTargetMet: List<Boolean>
) {
    fun dayLabels(locale: Locale = Locale("pl", "PL")): List<String> {
        val formatter = DateTimeFormatter.ofPattern("d MMM", locale)
        return (0 until 7).map { index ->
            weekStartDate.plusDays(index.toLong()).format(formatter)
        }
    }
    
    companion object {
        fun empty(referenceDate: LocalDate = LocalDate.now()): WeeklyStats {
            val start = referenceDate.minusDays(referenceDate.dayOfWeek.value.toLong() - 1)
            return WeeklyStats(
                weekStartDate = start,
                dailyMinutes = List(7) { 0 },
                dailyTargetMet = List(7) { false }
            )
        }
    }
}
