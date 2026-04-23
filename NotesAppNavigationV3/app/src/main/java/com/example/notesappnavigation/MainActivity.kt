package com.example.notesappnavigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.notesappnavigation.database.SettingsDataStore
import com.example.notesappnavigation.ui.theme.NotesAppNavigationTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val settingsDataStore: SettingsDataStore by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkMode by settingsDataStore.isDarkMode.collectAsState(initial = false)

            NotesAppNavigationTheme(darkTheme = isDarkMode) {
                MainScreen()
            }
        }
    }
}
