package com.khoihealth.app.health.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_steps")
data class StepsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val timestamp: Long,
    val steps: Int,
    val calories: Int,
    val distanceMeters: Int,
    val activeMinutes: Int,
    val synced: Boolean = false
)

@Entity(tableName = "heart_rate")
data class HeartRateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val timestamp: Long,
    val bpm: Int,
    val measurementType: String = "CONTINUOUS",
    val synced: Boolean = false
)

@Entity(tableName = "sleep_records")
data class SleepEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val startTimestamp: Long,
    val endTimestamp: Long,
    val deepSleepMinutes: Int,
    val lightSleepMinutes: Int,
    val remSleepMinutes: Int,
    val awakeMinutes: Int,
    val totalMinutes: Int,
    val sleepScore: Int = 0,
    val synced: Boolean = false
)

@Entity(tableName = "spo2_records")
data class SpO2Entity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val timestamp: Long,
    val percentage: Int,
    val synced: Boolean = false
)

@Entity(tableName = "blood_pressure")
data class BloodPressureEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val timestamp: Long,
    val systolic: Int,
    val diastolic: Int,
    val pulse: Int,
    val synced: Boolean = false
)

@Entity(tableName = "temperature")
data class TemperatureEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val timestamp: Long,
    val celsius: Float,
    val synced: Boolean = false
)
