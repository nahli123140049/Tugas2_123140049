package com.rapi.pocketwise

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.rapi.pocketwise.presentation.PocketWiseScreen
import com.rapi.pocketwise.presentation.PocketWiseViewModel

@Composable
fun App(
    geminiApiKey: String
) {
    // Pass geminiApiKey to ViewModel
    val viewModel = remember { PocketWiseViewModel(geminiApiKey) }
    val uiState by viewModel.uiState.collectAsState()

    val darkColors = darkColorScheme(
        primary = Color(0xFF5D6D9A),
        background = Color(0xFF121212),
        surface = Color(0xFF1E1E1E),
        onPrimary = Color.White,
        onBackground = Color.White,
        onSurface = Color.White
    )

    val lightColors = lightColorScheme(
        primary = Color(0xFF5D6D9A),
        background = Color.White,
        surface = Color.White,
        onPrimary = Color.White,
        onBackground = Color.Black,
        onSurface = Color.Black
    )

    MaterialTheme(
        colorScheme = if (uiState.isDarkMode) darkColors else lightColors
    ) {
        PocketWiseScreen(viewModel = viewModel)
    }
}
