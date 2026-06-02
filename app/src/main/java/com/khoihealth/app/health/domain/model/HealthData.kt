package com.khoihealth.app.health.domain.model

data class DailySteps(
    val steps: Int,
    val calories: Int,
    val distanceKm: Double,
    val activeMinutes: Int,
    val timestamp: Long
)

data class HeartRateReading(
    val bpm: Int,
    val timestamp: Long,
    val type: String = "CONTINUOUS"
)

data class SleepRecord(
    val deepMinutes: Int,
    val lightMinutes: Int,
    val remMinutes: Int,
    val awakeMinutes: Int,
    val totalMinutes: Int,
    val sleepScore: Int,
    val startTimestamp: Long,
    val endTimestamp: Long
)

data class SpO2Reading(val percentage: Int, val timestamp: Long)
data class BloodPressureReading(val systolic: Int, val diastolic: Int, val pulse: Int, val timestamp: Long)
data class TemperatureReading(val celsius: Float, val timestamp: Long)

data class HealthSummary(
    val steps: DailySteps? = null,
    val latestHeartRate: HeartRateReading? = null,
    val latestSleep: SleepRecord? = null,
    val latestSpO2: SpO2Reading? = null,
    val latestBp: BloodPressureReading? = null,
    val latestTemp: TemperatureReading? = null,
    val batteryLevel: Int = -1
)
