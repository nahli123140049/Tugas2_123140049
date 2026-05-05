package com.rapi.pocketwise

import kotlinx.coroutines.flow.Flow

interface Platform {
    val name: String
    val model: String
    val manufacturer: String
    val osVersion: String
    
    fun getBatteryLevel(): Int
    fun getBatteryStatus(): String
    fun observeConnectivity(): Flow<Boolean>
    fun getCurrentTimeMillis(): Long
}

expect fun getPlatform(): Platform