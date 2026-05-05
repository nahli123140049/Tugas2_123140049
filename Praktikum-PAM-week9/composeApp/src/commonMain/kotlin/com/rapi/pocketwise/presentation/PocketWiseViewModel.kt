package com.rapi.pocketwise.presentation

import com.rapi.pocketwise.Platform
import com.rapi.pocketwise.data.model.Note
import com.rapi.pocketwise.data.remote.GeminiService
import com.rapi.pocketwise.getPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PocketWiseViewModel(apiKey: String) {
    private val platform: Platform = getPlatform()
    private val geminiService = GeminiService(apiKey)
    private val _uiState = MutableStateFlow(PocketWiseUiState())
    val uiState: StateFlow<PocketWiseUiState> = _uiState.asStateFlow()

    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    init {
        _uiState.update { it.copy(
            deviceModel = platform.model,
            deviceManufacturer = platform.manufacturer,
            osVersion = platform.osVersion,
            batteryLevel = platform.getBatteryLevel(),
            batteryStatus = platform.getBatteryStatus()
        ) }

        viewModelScope.launch {
            platform.observeConnectivity().collect { isOnline ->
                _uiState.update { it.copy(isOnline = isOnline) }
            }
        }
    }

    fun navigateTo(screen: Screen, note: Note? = null) {
        _uiState.update { it.copy(
            currentScreen = screen,
            selectedNote = note,
            noteTitleInput = note?.title ?: "",
            noteDescInput = note?.description ?: "",
            noteContentInput = note?.content ?: "",
            noteReminderInput = note?.reminder ?: "",
            errorMessage = null
        ) }
    }

    fun translateNoteContent(targetLanguage: String) {
        val currentContent = _uiState.value.noteContentInput.ifBlank { 
            _uiState.value.selectedNote?.content ?: "" 
        }
        
        if (currentContent.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val prompt = "Translate the following text to $targetLanguage. Provide only the translated text without any explanations: \n\n $currentContent"
            
            val result = geminiService.generateContent(prompt)
            
            result.onSuccess { translatedText ->
                _uiState.update { it.copy(
                    noteContentInput = translatedText,
                    isLoading = false
                ) }
            }.onFailure { error ->
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMessage = "Gagal Translate: ${error.message}"
                ) }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onTitleChanged(value: String) { 
        _uiState.update { it.copy(noteTitleInput = value, errorMessage = null) } 
    }
    fun onDescChanged(value: String) { _uiState.update { it.copy(noteDescInput = value) } }
    fun onContentChanged(value: String) { _uiState.update { it.copy(noteContentInput = value) } }
    fun onReminderChanged(value: String) { _uiState.update { it.copy(noteReminderInput = value) } }

    fun saveNote() {
        val currentState = _uiState.value
        if (currentState.noteTitleInput.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Judul catatan tidak boleh kosong!") }
            return
        }

        val now = platform.getCurrentTimeMillis()
        val newNote = Note(
            id = currentState.selectedNote?.id ?: now.toString(),
            title = currentState.noteTitleInput,
            description = currentState.noteDescInput,
            content = currentState.noteContentInput,
            reminder = currentState.noteReminderInput,
            isFavorite = currentState.selectedNote?.isFavorite ?: false,
            createdAt = currentState.selectedNote?.createdAt ?: now
        )

        val updatedNotes = if (currentState.selectedNote == null) {
            currentState.notes + newNote
        } else {
            currentState.notes.map { if (it.id == newNote.id) newNote else it }
        }

        _uiState.update { it.copy(
            notes = updatedNotes,
            currentScreen = Screen.NOTES,
            errorMessage = null
        ) }
    }

    fun toggleFavorite(note: Note) {
        val updatedNotes = _uiState.value.notes.map {
            if (it.id == note.id) it.copy(isFavorite = !it.isFavorite) else it
        }
        _uiState.update { it.copy(notes = updatedNotes) }
    }

    fun deleteNote(note: Note) {
        val updatedNotes = _uiState.value.notes.filter { it.id != note.id }
        _uiState.update { it.copy(notes = updatedNotes) }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(isDarkMode = enabled) }
    }

    fun setSortOrder(newest: Boolean) {
        _uiState.update { it.copy(sortNewest = newest) }
    }
    
    fun refreshBatteryInfo() {
        _uiState.update { it.copy(
            batteryLevel = platform.getBatteryLevel(),
            batteryStatus = platform.getBatteryStatus()
        ) }
    }
}
