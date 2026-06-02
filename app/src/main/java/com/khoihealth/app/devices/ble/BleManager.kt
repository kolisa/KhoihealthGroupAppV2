package com.khoihealth.app.devices.ble

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.zeroner.blemidautumn.bean.WristBand
import com.zeroner.blemidautumn.bluetooth.IDataReceiveHandler
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK
import com.zeroner.blemidautumn.bluetooth.impl.BleService
import com.zeroner.blemidautumn.task.BackgroundThreadManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BleManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _bleState = MutableStateFlow<BleState>(BleState.Idle)
    val bleState: StateFlow<BleState> = _bleState.asStateFlow()

    private val _scannedDevices = MutableStateFlow<List<WristBand>>(emptyList())
    val scannedDevices: StateFlow<List<WristBand>> = _scannedDevices.asStateFlow()

    private val _connectedDevice = MutableStateFlow<WristBand?>(null)
    val connectedDevice: StateFlow<WristBand?> = _connectedDevice.asStateFlow()

    private val _deviceInfo = MutableStateFlow<DeviceInfo?>(null)
    val deviceInfo: StateFlow<DeviceInfo?> = _deviceInfo.asStateFlow()

    private val _batteryLevel = MutableStateFlow(-1)
    val batteryLevel: StateFlow<Int> = _batteryLevel.asStateFlow()

    private val _bleReady = MutableStateFlow(false)
    val bleReady: StateFlow<Boolean> = _bleReady.asStateFlow()

    private var serviceReady = false

    private val bleDataReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context, intent: Intent) {
            when (intent.action) {
                ACTION_CONNECT_STATE -> {
                    val connected = intent.getBooleanExtra(EXTRA_CONNECT_STATE, false)
                    if (connected) {
                        _connectedDevice.value = SuperBleSDK.createInstance(context).getWristBand()
                        _bleState.value = BleState.Connected
                    } else {
                        _bleReady.value = false
                        _bleState.value = BleState.Disconnected
                    }
                }
                ACTION_BLUETOOTH_INIT -> {
                    _bleReady.value = true
                    _bleState.value = BleState.Ready
                    onDeviceReady()
                }
                ACTION_BLUETOOTH_ERROR -> {
                    _bleState.value = BleState.Error("Bluetooth error")
                }
            }
        }
    }

    init {
        registerSdkListener()
        registerBroadcastReceiver()
    }

    private fun registerSdkListener() {
        SuperBleSDK.addBleListener(context, object : IDataReceiveHandler {
            override fun onScanResult(device: WristBand) {
                val current = _scannedDevices.value.toMutableList()
                if (current.none { it.address == device.address }) {
                    current.add(device)
                    _scannedDevices.value = current
                }
                LocalBroadcastManager.getInstance(context).sendBroadcast(
                    Intent(ACTION_SCAN_RESULT).putExtra(EXTRA_SCAN_DEVICE, device)
                )
            }

            override fun connectStatue(isConnect: Boolean) {
                LocalBroadcastManager.getInstance(context).sendBroadcast(
                    Intent(ACTION_CONNECT_STATE).putExtra(EXTRA_CONNECT_STATE, isConnect)
                )
            }

            override fun onBluetoothInit() {
                LocalBroadcastManager.getInstance(context).sendBroadcast(Intent(ACTION_BLUETOOTH_INIT))
            }

            override fun onDataArrived(sdkType: Int, dataType: Int, data: String) {
                LocalBroadcastManager.getInstance(context).sendBroadcast(
                    Intent(ACTION_DATA_ARRIVED)
                        .putExtra(EXTRA_SDK_TYPE, sdkType)
                        .putExtra(EXTRA_DATA_TYPE, dataType)
                        .putExtra(EXTRA_DATA, data)
                )
            }

            override fun onBluetoothError() {
                LocalBroadcastManager.getInstance(context).sendBroadcast(Intent(ACTION_BLUETOOTH_ERROR))
            }

            override fun onDiscoverService(serviceUUID: String) {}
            override fun onDiscoverCharacter(characterUUID: String) {}
            override fun onCommonSend(data: ByteArray) {}
            override fun onCmdReceive(data: ByteArray) {}
            override fun onCharacteristicChange(address: String) {}
            override fun onPreConnect() {}
            override fun noCallback() {}
            override fun onConnectionStateChanged(state: Int, newState: Int) {}
            override fun onSdkAutoReconnectTimesOut() {}
        })
    }

    private fun registerBroadcastReceiver() {
        val filter = IntentFilter().apply {
            addAction(ACTION_CONNECT_STATE)
            addAction(ACTION_BLUETOOTH_INIT)
            addAction(ACTION_BLUETOOTH_ERROR)
        }
        LocalBroadcastManager.getInstance(context).registerReceiver(bleDataReceiver, filter)
    }

    fun onServiceConnected(service: BleService) {
        serviceReady = true
        _bleState.value = BleState.ServiceReady
    }

    fun onServiceDisconnected() {
        serviceReady = false
        _bleReady.value = false
        _bleState.value = BleState.Idle
    }

    fun startScan() {
        if (!serviceReady) return
        _scannedDevices.value = emptyList()
        _bleState.value = BleState.Scanning
        SuperBleSDK.createInstance(context).startScan3()
    }

    fun stopScan() {
        if (serviceReady) SuperBleSDK.createInstance(context).stopScan()
        if (_bleState.value is BleState.Scanning) {
            _bleState.value = BleState.ServiceReady
        }
    }

    fun connect(device: WristBand) {
        if (!serviceReady) return
        _bleState.value = BleState.Connecting
        SuperBleSDK.createInstance(context).apply {
            setWristBand(device)
            setNeedReconnect(true)
            connect()
        }
    }

    fun disconnect() {
        val address = _connectedDevice.value?.address ?: return
        SuperBleSDK.createInstance(context).setNeedReconnect(false)
        BackgroundThreadManager.getInstance().clearQueue()
        SuperBleSDK.createInstance(context).disconnect(address, false)
        _connectedDevice.value = null
        _bleReady.value = false
        _bleState.value = BleState.Disconnected
    }

    fun unbind() {
        if (!serviceReady) return
        BackgroundThreadManager.getInstance().clearQueue()
        SuperBleSDK.createInstance(context).unbindDevice()
        _connectedDevice.value = null
        _bleReady.value = false
        _bleState.value = BleState.Disconnected
    }

    fun requestBattery() {
        sendCommand { getBattery() }
    }

    fun syncTime() {
        sendCommand { setTime() }
    }

    fun requestFirmwareInfo() {
        sendCommand { getFirmwareInformation() }
    }

    fun updateBatteryLevel(level: Int) {
        _batteryLevel.value = level
        _deviceInfo.value = _deviceInfo.value?.copy(batteryLevel = level)
            ?: _connectedDevice.value?.let { DeviceInfo(it.address, it.name, batteryLevel = level) }
    }

    fun updateFirmwareVersion(version: String) {
        val device = _connectedDevice.value ?: return
        _deviceInfo.value = (_deviceInfo.value ?: DeviceInfo(device.address, device.name))
            .copy(firmwareVersion = version)
    }

    fun isConnected(): Boolean = serviceReady && SuperBleSDK.createInstance(context).isConnected()
    fun isScanning(): Boolean = serviceReady && SuperBleSDK.createInstance(context).isScanning()

    private fun onDeviceReady() {
        syncTime()
        requestBattery()
        requestFirmwareInfo()
    }

    private fun sendCommand(block: Any.() -> ByteArray?) {
        if (!_bleReady.value || !serviceReady) return
        try {
            val impl = SuperBleSDK.getSDKSendBluetoothCmdImpl(context)
            val bytes = impl.block() ?: return
            BackgroundThreadManager.getInstance().addWriteData(context, bytes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun destroy() {
        try {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(bleDataReceiver)
        } catch (_: Exception) {}
    }

    companion object {
        const val ACTION_SCAN_RESULT      = "com.zeroner.app.ON_SCAN_RESULT"
        const val ACTION_CONNECT_STATE    = "com.zeroner.app.ON_CONNECT_STATUE"
        const val ACTION_DATA_ARRIVED     = "com.zeroner.app.ON_DATA_ARRIVED"
        const val ACTION_BLUETOOTH_INIT   = "com.zeroner.app.ON_BLUETOOTH_INIT"
        const val ACTION_BLUETOOTH_ERROR  = "com.zeroner.app.ON_BLUETOOTH_ERROR"
        const val EXTRA_SDK_TYPE          = "com.zeroner.app.BLE_SDK_TYPE"
        const val EXTRA_DATA_TYPE         = "com.zeroner.app.BLE_DATA_TYPE"
        const val EXTRA_DATA              = "com.zeroner.app.BLE_ARRIVED_DATA"
        const val EXTRA_SCAN_DEVICE       = "com.zeroner.app.BLE_SCAN_RESULT_DEVICE"
        const val EXTRA_CONNECT_STATE     = "com.zeroner.app.BLE_CONNECT_STAUE"
    }
}
