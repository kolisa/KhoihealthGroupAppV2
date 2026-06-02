package com.khoihealth.app.goals.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khoihealth.app.auth.data.local.UserPreferences
import com.khoihealth.app.goals.domain.model.Goal
import com.khoihealth.app.goals.domain.repository.GoalsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val goalsRepository: GoalsRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val goals: StateFlow<Goal> = userPreferences.userId
        .flatMapLatest { uid ->
            if (uid != null) goalsRepository.getGoals(uid).map { it ?: Goal() }
            else flowOf(Goal())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Goal())

    fun saveGoals(goal: Goal) {
        viewModelScope.launch {
            val userId = userPreferences.getUserId() ?: return@launch
            goalsRepository.saveGoals(userId, goal)
        }
    }
}
