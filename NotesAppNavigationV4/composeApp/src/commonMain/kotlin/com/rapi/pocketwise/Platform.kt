package com.rapi.pocketwise

interface Platform {
    val name: String
    val model: String
    val manufacturer: String
}

expect fun getPlatform(): Platform

data class BatteryInfo(
    val level: Int,
    val isCharging: Boolean
)

interface DeviceManager {
    fun getBatteryInfo(): BatteryInfo
    fun isOnline(): Boolean
}

expect fun getDeviceManager(): DeviceManager
