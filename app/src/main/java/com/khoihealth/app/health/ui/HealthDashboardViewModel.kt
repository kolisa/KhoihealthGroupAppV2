package com.khoihealth.app.health.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khoihealth.app.auth.data.local.UserPreferences
import com.khoihealth.app.devices.ble.BleManager
import com.khoihealth.app.devices.ble.BleState
import com.khoihealth.app.goals.domain.model.Goal
import com.khoihealth.app.goals.domain.repository.GoalsRepository
import com.khoihealth.app.health.domain.model.HealthSummary
import com.khoihealth.app.health.domain.repository.HealthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class DashboardUiState(
    val summary: HealthSummary = HealthSummary(),
    val goals: Goal = Goal(),
    val bleState: BleState = BleState.Idle,
    val batteryLevel: Int = -1,
    val userName: String = "",
    val isLoading: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HealthDashboardViewModel @Inject constructor(
    private val healthRepository: HealthRepository,
    private val goalsRepository: GoalsRepository,
    private val bleManager: BleManager,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        userPreferences.userId.flatMapLatest { userId ->
            if (userId != null) {
                combine(
                    healthRepository.getTodaySummary(userId),
                    goalsRepository.getGoals(userId)
                ) { summary, goal ->
                    Pair(summary, goal ?: Goal())
                }
            } else {
                flowOf(Pair(HealthSummary(), Goal()))
            }
        },
        bleManager.bleState,
        bleManager.batteryLevel,
        userPreferences.userName
    ) { (summary, goal), bleState, battery, userName ->
        DashboardUiState(
            summary = summary,
            goals = goal,
            bleState = bleState,
            batteryLevel = battery,
            userName = userName ?: ""
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DashboardUiState()
    )

    fun syncDevice() {
        if (bleManager.isConnected()) {
            bleManager.requestBattery()
        }
    }
}
