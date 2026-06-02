package com.khoihealth.app.reports.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khoihealth.app.auth.data.local.UserPreferences
import com.khoihealth.app.health.domain.model.DailySteps
import com.khoihealth.app.health.domain.model.HeartRateReading
import com.khoihealth.app.health.domain.model.SleepRecord
import com.khoihealth.app.reports.domain.repository.ReportsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ReportsUiState(
    val weeklySteps: List<DailySteps> = emptyList(),
    val weeklyHeartRate: List<HeartRateReading> = emptyList(),
    val weeklySleep: List<SleepRecord> = emptyList(),
    val avgSteps: Int = 0,
    val avgHeartRate: Int = 0,
    val avgSleepMinutes: Int = 0
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val reportsRepository: ReportsRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val uiState: StateFlow<ReportsUiState> = userPreferences.userId
        .flatMapLatest { uid ->
            if (uid == null) return@flatMapLatest flowOf(ReportsUiState())
            combine(
                reportsRepository.getWeeklySteps(uid),
                reportsRepository.getWeeklyHeartRate(uid),
                reportsRepository.getWeeklySleep(uid)
            ) { steps, hr, sleep ->
                ReportsUiState(
                    weeklySteps = steps,
                    weeklyHeartRate = hr,
                    weeklySleep = sleep,
                    avgSteps = if (steps.isEmpty()) 0 else steps.sumOf { it.steps } / steps.size,
                    avgHeartRate = if (hr.isEmpty()) 0 else hr.sumOf { it.bpm } / hr.size,
                    avgSleepMinutes = if (sleep.isEmpty()) 0 else sleep.sumOf { it.totalMinutes } / sleep.size
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReportsUiState())
}
