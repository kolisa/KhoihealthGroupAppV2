package com.khoihealth.app.devices.domain.repository

import com.khoihealth.app.devices.domain.model.Device
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    fun getBoundDevice(userId: String): Flow<Device?>
    fun getUserDevices(userId: String): Flow<List<Device>>
    suspend fun bindDevice(device: Device, userId: String)
    suspend fun unbindDevice(address: String, userId: String)
    suspend fun updateBattery(address: String, level: Int)
    suspend fun updateFirmware(address: String, version: String)
}
