package com.rapi.NoteAppsNavigationV4

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.rapi.NoteAppsNavigationV4.data.remote.GeminiService
import com.rapi.NoteAppsNavigationV4.data.repository.FinanceRepositoryImpl
import com.rapi.NoteAppsNavigationV4.presentation.PocketWiseScreen
import com.rapi.NoteAppsNavigationV4.presentation.PocketWiseViewModel

@Composable
fun App(
    geminiApiKey: String
) {
    MaterialTheme {
        val viewModel = remember {
            val geminiService = GeminiService(
                apiKey = geminiApiKey
            )

            val financeRepository = FinanceRepositoryImpl(
                geminiService = geminiService
            )

            PocketWiseViewModel(
                repository = financeRepository
            )
        }

        PocketWiseScreen(
            viewModel = viewModel
        )
    }
}
