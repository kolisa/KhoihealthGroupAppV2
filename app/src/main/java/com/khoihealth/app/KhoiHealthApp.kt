package com.khoihealth.app

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.khoihealth.app.devices.ble.BleManager
import com.zeroner.blemidautumn.bluetooth.IBle
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK
import com.zeroner.blemidautumn.bluetooth.impl.BleService
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class KhoiHealthApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var bleManager: BleManager

    private var iBle: IBle? = null
    private var bleServiceBound = false

    private val bleServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            try {
                val service = (binder as BleService.LocalBinder).getService()
                iBle = service.getBle()
                bleServiceBound = true
                bleManager.onServiceConnected(service)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            iBle = null
            bleServiceBound = false
            bleManager.onServiceDisconnected()
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initBleSDK()
    }

    private fun initBleSDK() {
        SuperBleSDK.getInstance().init(this)
        val serviceIntent = Intent(this, BleService::class.java)
        stopService(serviceIntent)
        bindService(serviceIntent, bleServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    fun getIBle(): IBle? = iBle
    fun isBleServiceBound(): Boolean = bleServiceBound

    override fun onTerminate() {
        super.onTerminate()
        if (bleServiceBound) {
            try { unbindService(bleServiceConnection) } catch (_: Exception) {}
        }
        bleManager.destroy()
    }

    companion object {
        lateinit var instance: KhoiHealthApp
            private set
    }
}
