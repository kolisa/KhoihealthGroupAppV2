package com.khoihealth.app.reports.domain.repository

import com.khoihealth.app.health.domain.model.DailySteps
import com.khoihealth.app.health.domain.model.HeartRateReading
import com.khoihealth.app.health.domain.model.SleepRecord
import kotlinx.coroutines.flow.Flow

interface ReportsRepository {
    fun getWeeklySteps(userId: String): Flow<List<DailySteps>>
    fun getWeeklyHeartRate(userId: String): Flow<List<HeartRateReading>>
    fun getWeeklySleep(userId: String): Flow<List<SleepRecord>>
}
