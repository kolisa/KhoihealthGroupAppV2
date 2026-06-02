package com.khoihealth.app.reports.data

import com.khoihealth.app.core.utils.DateUtils
import com.khoihealth.app.health.data.local.HealthDao
import com.khoihealth.app.health.domain.model.DailySteps
import com.khoihealth.app.health.domain.model.HeartRateReading
import com.khoihealth.app.health.domain.model.SleepRecord
import com.khoihealth.app.reports.domain.repository.ReportsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReportsRepositoryImpl @Inject constructor(
    private val healthDao: HealthDao
) : ReportsRepository {

    override fun getWeeklySteps(userId: String): Flow<List<DailySteps>> {
        val from = DateUtils.weekStartMillis()
        val to = DateUtils.todayEndMillis()
        return healthDao.getStepsBetween(userId, from, to).map { list ->
            list.map { DailySteps(it.steps, it.calories, it.distanceMeters / 1000.0, it.activeMinutes, it.timestamp) }
        }
    }

    override fun getWeeklyHeartRate(userId: String): Flow<List<HeartRateReading>> {
        val from = DateUtils.weekStartMillis()
        val to = DateUtils.todayEndMillis()
        return healthDao.getHeartRateBetween(userId, from, to).map { list ->
            list.map { HeartRateReading(it.bpm, it.timestamp) }
        }
    }

    override fun getWeeklySleep(userId: String): Flow<List<SleepRecord>> {
        return healthDao.getRecentSleep(userId).map { list ->
            list.map {
                SleepRecord(it.deepSleepMinutes, it.lightSleepMinutes, it.remSleepMinutes,
                    it.awakeMinutes, it.totalMinutes, it.sleepScore, it.startTimestamp, it.endTimestamp)
            }
        }
    }
}
