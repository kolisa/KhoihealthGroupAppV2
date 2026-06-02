package com.khoihealth.app.goals.domain.repository

import com.khoihealth.app.goals.domain.model.Goal
import kotlinx.coroutines.flow.Flow

interface GoalsRepository {
    fun getGoals(userId: String): Flow<Goal?>
    suspend fun saveGoals(userId: String, goal: Goal)
}
