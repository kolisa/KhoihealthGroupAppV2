package com.khoihealth.app.devices.domain.model

data class Device(
    val address: String,
    val name: String,
    val firmwareVersion: String? = null,
    val batteryLevel: Int = -1,
    val isBound: Boolean = false,
    val lastConnected: Long = 0L,
    val rssi: Int = 0,
    val supportsHR: Boolean = true,
    val supportsSleep: Boolean = true,
    val supportsSpO2: Boolean = false,
    val supportsBP: Boolean = false
)
