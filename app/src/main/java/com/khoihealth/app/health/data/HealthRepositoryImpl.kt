package com.khoihealth.app.health.data

import com.khoihealth.app.core.utils.DateUtils
import com.khoihealth.app.health.data.local.HeartRateEntity
import com.khoihealth.app.health.data.local.HealthDao
import com.khoihealth.app.health.data.local.SleepEntity
import com.khoihealth.app.health.data.local.StepsEntity
import com.khoihealth.app.health.data.remote.HealthApiService
import com.khoihealth.app.health.data.remote.SyncHeartRateBody
import com.khoihealth.app.health.data.remote.SyncSleepBody
import com.khoihealth.app.health.data.remote.SyncStepsBody
import com.khoihealth.app.health.domain.model.BloodPressureReading
import com.khoihealth.app.health.domain.model.DailySteps
import com.khoihealth.app.health.domain.model.HeartRateReading
import com.khoihealth.app.health.domain.model.HealthSummary
import com.khoihealth.app.health.domain.model.SleepRecord
import com.khoihealth.app.health.domain.model.SpO2Reading
import com.khoihealth.app.health.domain.model.TemperatureReading
import com.khoihealth.app.health.domain.repository.HealthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class HealthRepositoryImpl @Inject constructor(
    private val healthDao: HealthDao,
    private val healthApiService: HealthApiService
) : HealthRepository {

    override fun getTodaySummary(userId: String): Flow<HealthSummary> {
        val from = DateUtils.todayStartMillis()
        val to = DateUtils.todayEndMillis()
        return combine(
            healthDao.getLatestSteps(userId),
            healthDao.getLatestHeartRate(userId),
            healthDao.getLatestSleep(userId),
            healthDao.getLatestSpO2(userId),
            healthDao.getLatestBloodPressure(userId)
        ) { steps, hr, sleep, spo2, bp ->
            HealthSummary(
                steps = steps?.let {
                    DailySteps(it.steps, it.calories, it.distanceMeters / 1000.0, it.activeMinutes, it.timestamp)
                },
                latestHeartRate = hr?.let { HeartRateReading(it.bpm, it.timestamp, it.measurementType) },
                latestSleep = sleep?.let {
                    SleepRecord(it.deepSleepMinutes, it.lightSleepMinutes, it.remSleepMinutes,
                        it.awakeMinutes, it.totalMinutes, it.sleepScore, it.startTimestamp, it.endTimestamp)
                },
                latestSpO2 = spo2?.let { SpO2Reading(it.percentage, it.timestamp) },
                latestBp = bp?.let { BloodPressureReading(it.systolic, it.diastolic, it.pulse, it.timestamp) }
            )
        }
    }

    override fun getRecentHeartRates(userId: String, from: Long, to: Long): Flow<List<HeartRateEntity>> =
        healthDao.getHeartRateBetween(userId, from, to)

    override fun getRecentSleep(userId: String): Flow<List<SleepEntity>> =
        healthDao.getRecentSleep(userId)

    override fun getStepsHistory(userId: String, from: Long, to: Long): Flow<List<StepsEntity>> =
        healthDao.getStepsBetween(userId, from, to)

    override suspend fun saveSteps(steps: StepsEntity) = healthDao.insertSteps(steps)

    override suspend fun saveHeartRate(hr: HeartRateEntity) = healthDao.insertHeartRate(hr)

    override suspend fun saveSleep(sleep: SleepEntity) = healthDao.insertSleep(sleep)

    override suspend fun syncPendingToServer(): Boolean {
        return try {
            val unsyncedSteps = healthDao.getUnsyncedSteps()
            if (unsyncedSteps.isNotEmpty()) {
                val body = SyncStepsBody(unsyncedSteps.map {
                    mapOf("timestamp" to it.timestamp, "steps" to it.steps,
                        "calories" to it.calories, "distance" to it.distanceMeters)
                })
                if (healthApiService.syncSteps(body).isSuccessful) {
                    healthDao.markStepsSynced(unsyncedSteps.map { it.id })
                }
            }

            val unsyncedHR = healthDao.getUnsyncedHeartRates()
            if (unsyncedHR.isNotEmpty()) {
                val body = SyncHeartRateBody(unsyncedHR.map {
                    mapOf("timestamp" to it.timestamp, "bpm" to it.bpm, "type" to it.measurementType)
                })
                if (healthApiService.syncHeartRate(body).isSuccessful) {
                    healthDao.markHeartRateSynced(unsyncedHR.map { it.id })
                }
            }

            val unsyncedSleep = healthDao.getUnsyncedSleep()
            if (unsyncedSleep.isNotEmpty()) {
                val body = SyncSleepBody(unsyncedSleep.map {
                    mapOf("start" to it.startTimestamp, "end" to it.endTimestamp,
                        "deep" to it.deepSleepMinutes, "light" to it.lightSleepMinutes,
                        "total" to it.totalMinutes)
                })
                if (healthApiService.syncSleep(body).isSuccessful) {
                    healthDao.markSleepSynced(unsyncedSleep.map { it.id })
                }
            }

            true
        } catch (e: Exception) {
            false
        }
    }
}
