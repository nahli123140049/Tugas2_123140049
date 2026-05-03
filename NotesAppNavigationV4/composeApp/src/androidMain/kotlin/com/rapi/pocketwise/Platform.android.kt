package com.rapi.pocketwise

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val model: String = Build.MODEL
    override val manufacturer: String = Build.MANUFACTURER
}

actual fun getPlatform(): Platform = AndroidPlatform()

class AndroidDeviceManager(private val context: Context) : DeviceManager {
    override fun getBatteryInfo(): BatteryInfo {
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        
        val batteryStatusIntent = context.registerReceiver(null, android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED))
        val status = batteryStatusIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL
        
        return BatteryInfo(level, isCharging)
    }

    override fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

private var appContext: Context? = null

fun initPlatform(context: Context) {
    appContext = context
}

actual fun getDeviceManager(): DeviceManager {
    return AndroidDeviceManager(appContext ?: throw IllegalStateException("Context not initialized. Call initPlatform in MainActivity."))
}
