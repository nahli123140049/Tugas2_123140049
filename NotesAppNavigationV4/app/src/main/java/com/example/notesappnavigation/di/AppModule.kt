package com.example.notesappnavigation.di

import com.example.notesappnavigation.ai.AiRepository
import com.example.notesappnavigation.ai.AiViewModel
import com.example.notesappnavigation.ai.ChatViewModel
import com.example.notesappnavigation.ai.GeminiClient
import com.example.notesappnavigation.database.NoteRepository
import com.example.notesappnavigation.database.NoteViewModel
import com.example.notesappnavigation.database.SettingsDataStore
import com.example.notesappnavigation.platform.*
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { NoteRepository(androidContext()) }
    single { SettingsDataStore(androidContext()) }

    // Platform Features
    single<DeviceInfo> { AndroidDeviceInfo() }
    single<NetworkMonitor> { AndroidNetworkMonitor(androidContext()) }
    single<BatteryInfo> { AndroidBatteryInfo(androidContext()) }

    // AI
    single { GeminiClient() }
    single { AiRepository(get()) }

    viewModel { NoteViewModel(get()) }
    viewModel { AiViewModel(get()) }
    viewModel { ChatViewModel(get()) }
}
