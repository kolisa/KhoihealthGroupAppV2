package com.khoihealth.app.devices.ble

sealed class BleState {
    object Idle : BleState()
    object ServiceReady : BleState()
    object Scanning : BleState()
    object Connecting : BleState()
    object Connected : BleState()
    object Ready : BleState()            // BT init complete, ready for commands
    object Disconnected : BleState()
    data class Error(val message: String) : BleState()
}

data class DeviceInfo(
    val address: String,
    val name: String,
    val firmwareVersion: String? = null,
    val hardwareVersion: String? = null,
    val batteryLevel: Int = -1
)
