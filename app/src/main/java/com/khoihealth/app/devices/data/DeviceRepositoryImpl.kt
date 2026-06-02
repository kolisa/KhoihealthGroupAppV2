package com.khoihealth.app.devices.data

import com.khoihealth.app.devices.data.local.DeviceDao
import com.khoihealth.app.devices.data.local.DeviceEntity
import com.khoihealth.app.devices.domain.model.Device
import com.khoihealth.app.devices.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DeviceRepositoryImpl @Inject constructor(
    private val deviceDao: DeviceDao
) : DeviceRepository {

    override fun getBoundDevice(userId: String): Flow<Device?> =
        deviceDao.getBoundDevice(userId).map { it?.toDomain() }

    override fun getUserDevices(userId: String): Flow<List<Device>> =
        deviceDao.getDevicesForUser(userId).map { list -> list.map { it.toDomain() } }

    override suspend fun bindDevice(device: Device, userId: String) {
        deviceDao.unbindAllForUser(userId)
        deviceDao.insertDevice(
            DeviceEntity(
                address = device.address,
                name = device.name,
                userId = userId,
                isBound = true,
                lastConnected = System.currentTimeMillis()
            )
        )
    }

    override suspend fun unbindDevice(address: String, userId: String) {
        deviceDao.getDeviceByAddress(address)?.let {
            deviceDao.updateDevice(it.copy(isBound = false))
        }
    }

    override suspend fun updateBattery(address: String, level: Int) =
        deviceDao.updateBattery(address, level)

    override suspend fun updateFirmware(address: String, version: String) =
        deviceDao.updateFirmware(address, version)

    private fun DeviceEntity.toDomain() = Device(
        address = address,
        name = name,
        firmwareVersion = firmwareVersion,
        batteryLevel = batteryLevel,
        isBound = isBound,
        lastConnected = lastConnected,
        supportsHR = supportsHR,
        supportsSleep = supportsSleep,
        supportsSpO2 = supportsSpO2,
        supportsBP = supportsBP
    )
}
