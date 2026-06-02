package com.khoihealth.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.khoihealth.app.devices.data.local.DeviceDao
import com.khoihealth.app.devices.data.local.DeviceEntity
import com.khoihealth.app.goals.data.local.GoalDao
import com.khoihealth.app.goals.data.local.GoalEntity
import com.khoihealth.app.health.data.local.BloodPressureEntity
import com.khoihealth.app.health.data.local.HeartRateEntity
import com.khoihealth.app.health.data.local.HealthDao
import com.khoihealth.app.health.data.local.SleepEntity
import com.khoihealth.app.health.data.local.SpO2Entity
import com.khoihealth.app.health.data.local.StepsEntity
import com.khoihealth.app.health.data.local.TemperatureEntity

@Database(
    entities = [
        DeviceEntity::class,
        StepsEntity::class,
        HeartRateEntity::class,
        SleepEntity::class,
        SpO2Entity::class,
        BloodPressureEntity::class,
        TemperatureEntity::class,
        GoalEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class KhoiHealthDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun healthDao(): HealthDao
    abstract fun goalDao(): GoalDao

    companion object {
        const val DATABASE_NAME = "khoi_health_db"
    }
}
