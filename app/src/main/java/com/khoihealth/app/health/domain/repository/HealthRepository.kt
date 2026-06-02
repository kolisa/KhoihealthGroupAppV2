package com.khoihealth.app.health.domain.repository

import com.khoihealth.app.health.data.local.HeartRateEntity
import com.khoihealth.app.health.data.local.SleepEntity
import com.khoihealth.app.health.data.local.StepsEntity
import com.khoihealth.app.health.domain.model.HealthSummary
import kotlinx.coroutines.flow.Flow

interface HealthRepository {
    fun getTodaySummary(userId: String): Flow<HealthSummary>
    fun getRecentHeartRates(userId: String, from: Long, to: Long): Flow<List<HeartRateEntity>>
    fun getRecentSleep(userId: String): Flow<List<SleepEntity>>
    fun getStepsHistory(userId: String, from: Long, to: Long): Flow<List<StepsEntity>>
    suspend fun saveSteps(steps: StepsEntity)
    suspend fun saveHeartRate(hr: HeartRateEntity)
    suspend fun saveSleep(sleep: SleepEntity)
    suspend fun syncPendingToServer(): Boolean
}
