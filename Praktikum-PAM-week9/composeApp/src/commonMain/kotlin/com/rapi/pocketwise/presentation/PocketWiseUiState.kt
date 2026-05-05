package com.rapi.pocketwise.presentation

import com.rapi.pocketwise.data.model.Note

enum class Screen {
    NOTES, FAVORITES, PROFILE, SETTINGS, EDIT_NOTE, DETAIL_NOTE
}

data class PocketWiseUiState(
    // Navigation
    val currentScreen: Screen = Screen.NOTES,
    
    // Notes Data
    val notes: List<Note> = emptyList(),
    val searchQuery: String = "",
    val selectedNote: Note? = null,
    
    // Form State (New/Edit Note)
    val noteTitleInput: String = "",
    val noteDescInput: String = "",
    val noteContentInput: String = "",
    val noteReminderInput: String = "",
    
    // Device/System State
    val isOnline: Boolean = true,
    val batteryLevel: Int = 0,
    val batteryStatus: String = "Unknown",
    val deviceModel: String = "",
    val deviceManufacturer: String = "",
    val osVersion: String = "",
    
    // Settings
    val isDarkMode: Boolean = true,
    val sortNewest: Boolean = true,
    
    // UI Feedback
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
