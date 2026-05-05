package com.example.notesappnavigation

import android.app.Application
import com.example.notesappnavigation.di.dataModule
import com.example.notesappnavigation.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NotesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@NotesApplication)
            modules(dataModule, viewModelModule)
        }
    }
}
