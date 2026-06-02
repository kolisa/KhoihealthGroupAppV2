package com.khoihealth.app.devices.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: DeviceEntity)

    @Update
    suspend fun updateDevice(device: DeviceEntity)

    @Query("SELECT * FROM devices WHERE userId = :userId")
    fun getDevicesForUser(userId: String): Flow<List<DeviceEntity>>

    @Query("SELECT * FROM devices WHERE userId = :userId AND isBound = 1 LIMIT 1")
    fun getBoundDevice(userId: String): Flow<DeviceEntity?>

    @Query("SELECT * FROM devices WHERE address = :address LIMIT 1")
    suspend fun getDeviceByAddress(address: String): DeviceEntity?

    @Query("UPDATE devices SET batteryLevel = :battery WHERE address = :address")
    suspend fun updateBattery(address: String, battery: Int)

    @Query("UPDATE devices SET firmwareVersion = :firmware WHERE address = :address")
    suspend fun updateFirmware(address: String, firmware: String)

    @Query("UPDATE devices SET isBound = 0 WHERE userId = :userId")
    suspend fun unbindAllForUser(userId: String)

    @Query("UPDATE devices SET lastConnected = :timestamp WHERE address = :address")
    suspend fun updateLastConnected(address: String, timestamp: Long)

    @Query("DELETE FROM devices WHERE address = :address")
    suspend fun deleteDevice(address: String)
}
