package com.khoihealth.app.health.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class SyncStepsBody(val records: List<Map<String, Any>>)
data class SyncHeartRateBody(val records: List<Map<String, Any>>)
data class SyncSleepBody(val records: List<Map<String, Any>>)

interface HealthApiService {
    @POST("health/steps/sync")
    suspend fun syncSteps(@Body body: SyncStepsBody): Response<Unit>

    @POST("health/heart-rate/sync")
    suspend fun syncHeartRate(@Body body: SyncHeartRateBody): Response<Unit>

    @POST("health/sleep/sync")
    suspend fun syncSleep(@Body body: SyncSleepBody): Response<Unit>

    @POST("health/spo2/sync")
    suspend fun syncSpO2(@Body body: Map<String, Any>): Response<Unit>

    @POST("health/blood-pressure/sync")
    suspend fun syncBloodPressure(@Body body: Map<String, Any>): Response<Unit>
}
