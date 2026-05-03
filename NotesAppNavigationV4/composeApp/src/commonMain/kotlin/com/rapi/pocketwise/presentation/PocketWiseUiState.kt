package com.rapi.pocketwise.presentation

import com.rapi.pocketwise.data.model.Note

data class PocketWiseUiState(
    val notes: List<Note> = listOf(
        Note(
            id = "1",
            title = "Kuis Visdat",
            description = "Kuis Visualisasi Data dan Informasi",
            content = "Belajar tentang grafik, dashboard, dan interpretasi data.",
            date = "30 April 2024",
            isFavorite = true
        ),
        Note(
            id = "2",
            title = "Tugas PAM 6",
            description = "NewsReaderApp",
            content = "Membuat aplikasi pembaca berita menggunakan API eksternal.",
            date = "30 April 2024"
        ),
        Note(
            id = "3",
            title = "Tugas PAM 7",
            description = "melanjutkan NotesApp",
            content = "Menambahkan fitur database lokal dan sinkronisasi cloud.",
            date = "30 April 2024"
        )
    ),
    val searchQuery: String = "",
    val currentScreen: Screen = Screen.Notes,
    val selectedNote: Note? = null,
    val isEditing: Boolean = false,
    
    // Form fields for adding/editing
    val titleInput: String = "",
    val descriptionInput: String = "",
    val contentInput: String = "",
    val reminderInput: String = "",

    val aiResult: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    
    val isOnline: Boolean = true,
    val isDarkMode: Boolean = true,
    val sortOrder: SortOrder = SortOrder.NEWEST,

    // Real-time device info
    val batteryLevel: String = "0%",
    val batteryStatus: String = "Unknown",
    val deviceModel: String = "Unknown",
    val deviceManufacturer: String = "Unknown",
    val osVersion: String = "Unknown",

    // Translation
    val selectedLanguage: String = "English"
)

enum class Screen {
    Notes, Favorites, Profile, Settings, Detail
}

enum class SortOrder {
    NEWEST, OLDEST
}
