package com.khoihealth.app.devices.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khoihealth.app.auth.data.local.UserPreferences
import com.khoihealth.app.devices.ble.BleManager
import com.khoihealth.app.devices.ble.BleState
import com.khoihealth.app.devices.domain.model.Device
import com.khoihealth.app.devices.domain.repository.DeviceRepository
import com.zeroner.blemidautumn.bean.WristBand
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DeviceUiState(
    val scannedDevices: List<WristBand> = emptyList(),
    val boundDevice: Device? = null,
    val bleState: BleState = BleState.Idle,
    val batteryLevel: Int = -1,
    val firmwareVersion: String? = null,
    val isLoading: Boolean = false,
    val message: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DeviceViewModel @Inject constructor(
    val bleManager: BleManager,
    private val deviceRepository: DeviceRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _message = MutableStateFlow<String?>(null)

    val uiState: StateFlow<DeviceUiState> = combine(
        bleManager.scannedDevices,
        bleManager.bleState,
        bleManager.batteryLevel,
        bleManager.deviceInfo,
        userPreferences.userId.flatMapLatest { uid ->
            if (uid != null) deviceRepository.getBoundDevice(uid) else flowOf(null)
        }
    ) { scanned, bleState, battery, deviceInfo, boundDevice ->
        DeviceUiState(
            scannedDevices = scanned,
            boundDevice = boundDevice,
            bleState = bleState,
            batteryLevel = battery,
            firmwareVersion = deviceInfo?.firmwareVersion,
            isLoading = bleState is BleState.Scanning || bleState is BleState.Connecting
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DeviceUiState())

    fun startScan() {
        bleManager.startScan()
        viewModelScope.launch {
            kotlinx.coroutines.delay(15_000)
            if (bleManager.isScanning()) bleManager.stopScan()
        }
    }

    fun stopScan() = bleManager.stopScan()

    fun connect(device: WristBand) {
        bleManager.stopScan()
        bleManager.connect(device)
        viewModelScope.launch {
            val userId = userPreferences.getUserId() ?: return@launch
            kotlinx.coroutines.delay(3000)
            if (bleManager.isConnected()) {
                deviceRepository.bindDevice(
                    Device(address = device.address, name = device.name, isBound = true),
                    userId
                )
            }
        }
    }

    fun disconnect() = bleManager.disconnect()

    fun unbind() {
        bleManager.unbind()
        viewModelScope.launch {
            val userId = userPreferences.getUserId() ?: return@launch
            val address = bleManager.connectedDevice.value?.address ?: return@launch
            deviceRepository.unbindDevice(address, userId)
        }
    }

    fun requestBattery() = bleManager.requestBattery()
    fun clearMessage() { _message.value = null }
}
