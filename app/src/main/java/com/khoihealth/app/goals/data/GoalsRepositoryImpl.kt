package com.khoihealth.app.goals.data

import com.khoihealth.app.goals.data.local.GoalDao
import com.khoihealth.app.goals.data.local.GoalEntity
import com.khoihealth.app.goals.domain.model.Goal
import com.khoihealth.app.goals.domain.repository.GoalsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GoalsRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao
) : GoalsRepository {

    override fun getGoals(userId: String): Flow<Goal?> =
        goalDao.getGoals(userId).map { it?.toDomain() }

    override suspend fun saveGoals(userId: String, goal: Goal) {
        goalDao.insertGoal(
            GoalEntity(
                userId = userId,
                dailySteps = goal.dailySteps,
                dailyCalories = goal.dailyCalories,
                sleepHours = goal.sleepHours,
                activeMinutes = goal.activeMinutes,
                waterMl = goal.waterMl
            )
        )
    }

    private fun GoalEntity.toDomain() = Goal(
        dailySteps = dailySteps,
        dailyCalories = dailyCalories,
        sleepHours = sleepHours,
        activeMinutes = activeMinutes,
        waterMl = waterMl
    )
}
