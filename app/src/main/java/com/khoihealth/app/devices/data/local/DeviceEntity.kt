package com.khoihealth.app.devices.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey val address: String,
    val name: String,
    val userId: String,
    val firmwareVersion: String? = null,
    val hardwareVersion: String? = null,
    val batteryLevel: Int = -1,
    val isBound: Boolean = false,
    val lastConnected: Long = 0L,
    val sdkType: Int = 0,
    val supportsHR: Boolean = true,
    val supportsSleep: Boolean = true,
    val supportsSpO2: Boolean = false,
    val supportsBP: Boolean = false,
    val supportsTemp: Boolean = false
)
