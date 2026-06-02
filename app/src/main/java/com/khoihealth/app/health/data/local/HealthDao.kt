package com.khoihealth.app.health.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthDao {

    // Steps
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: StepsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSteps(steps: List<StepsEntity>)

    @Query("SELECT * FROM daily_steps WHERE userId = :userId AND timestamp BETWEEN :from AND :to ORDER BY timestamp DESC")
    fun getStepsBetween(userId: String, from: Long, to: Long): Flow<List<StepsEntity>>

    @Query("SELECT * FROM daily_steps WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    fun getLatestSteps(userId: String): Flow<StepsEntity?>

    @Query("SELECT SUM(steps) FROM daily_steps WHERE userId = :userId AND timestamp BETWEEN :from AND :to")
    fun getTotalSteps(userId: String, from: Long, to: Long): Flow<Int?>

    @Query("UPDATE daily_steps SET synced = 1 WHERE id IN (:ids)")
    suspend fun markStepsSynced(ids: List<Long>)

    @Query("SELECT * FROM daily_steps WHERE synced = 0")
    suspend fun getUnsyncedSteps(): List<StepsEntity>

    // Heart rate
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHeartRate(hr: HeartRateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllHeartRates(hrs: List<HeartRateEntity>)

    @Query("SELECT * FROM heart_rate WHERE userId = :userId AND timestamp BETWEEN :from AND :to ORDER BY timestamp DESC")
    fun getHeartRateBetween(userId: String, from: Long, to: Long): Flow<List<HeartRateEntity>>

    @Query("SELECT * FROM heart_rate WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    fun getLatestHeartRate(userId: String): Flow<HeartRateEntity?>

    @Query("SELECT AVG(bpm) FROM heart_rate WHERE userId = :userId AND timestamp BETWEEN :from AND :to")
    fun getAvgHeartRate(userId: String, from: Long, to: Long): Flow<Double?>

    @Query("UPDATE heart_rate SET synced = 1 WHERE id IN (:ids)")
    suspend fun markHeartRateSynced(ids: List<Long>)

    @Query("SELECT * FROM heart_rate WHERE synced = 0")
    suspend fun getUnsyncedHeartRates(): List<HeartRateEntity>

    // Sleep
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleep(sleep: SleepEntity)

    @Query("SELECT * FROM sleep_records WHERE userId = :userId ORDER BY startTimestamp DESC LIMIT 7")
    fun getRecentSleep(userId: String): Flow<List<SleepEntity>>

    @Query("SELECT * FROM sleep_records WHERE userId = :userId ORDER BY startTimestamp DESC LIMIT 1")
    fun getLatestSleep(userId: String): Flow<SleepEntity?>

    @Query("UPDATE sleep_records SET synced = 1 WHERE id IN (:ids)")
    suspend fun markSleepSynced(ids: List<Long>)

    @Query("SELECT * FROM sleep_records WHERE synced = 0")
    suspend fun getUnsyncedSleep(): List<SleepEntity>

    // SpO2
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpO2(spo2: SpO2Entity)

    @Query("SELECT * FROM spo2_records WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    fun getLatestSpO2(userId: String): Flow<SpO2Entity?>

    @Query("SELECT * FROM spo2_records WHERE synced = 0")
    suspend fun getUnsyncedSpO2(): List<SpO2Entity>

    // Blood pressure
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBloodPressure(bp: BloodPressureEntity)

    @Query("SELECT * FROM blood_pressure WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    fun getLatestBloodPressure(userId: String): Flow<BloodPressureEntity?>

    @Query("SELECT * FROM blood_pressure WHERE synced = 0")
    suspend fun getUnsyncedBloodPressure(): List<BloodPressureEntity>

    // Temperature
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemperature(temp: TemperatureEntity)

    @Query("SELECT * FROM temperature WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    fun getLatestTemperature(userId: String): Flow<TemperatureEntity?>

    @Query("SELECT * FROM temperature WHERE synced = 0")
    suspend fun getUnsyncedTemperature(): List<TemperatureEntity>
}
