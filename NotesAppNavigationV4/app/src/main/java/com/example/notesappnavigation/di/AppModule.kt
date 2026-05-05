package com.example.notesappnavigation.di

import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.notesappnavigation.database.NoteDatabase
import com.example.notesappnavigation.database.NoteRepository
import com.example.notesappnavigation.database.NoteViewModel
import com.example.notesappnavigation.database.SettingsDataStore
import com.example.notesappnavigation.platform.*
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dataModule = module {
    single {
        val driver = AndroidSqliteDriver(NoteDatabase.Schema, androidContext(), "notes_v2.db")
        NoteDatabase(driver)
    }
    single { NoteRepository(get()) }
    single { SettingsDataStore(androidContext()) }
    
    // Platform Features
    single<DeviceInfo> { AndroidDeviceInfo() }
    single<NetworkMonitor> { AndroidNetworkMonitor(androidContext()) }
    single<BatteryInfo> { AndroidBatteryInfo(androidContext()) }
}

val viewModelModule = module {
    viewModel { NoteViewModel(get()) }
}
