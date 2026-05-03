package com.rapi.NoteAppsNavigationV4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPlatform(this)
        setContent {
            App(
                geminiApiKey = BuildConfig.GEMINI_API_KEY
            )
        }
    }
}
