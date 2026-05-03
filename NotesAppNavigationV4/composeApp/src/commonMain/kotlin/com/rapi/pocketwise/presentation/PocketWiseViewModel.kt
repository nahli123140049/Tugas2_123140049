package com.rapi.pocketwise.presentation

import com.rapi.pocketwise.data.model.Note
import com.rapi.pocketwise.data.repository.FinanceRepository
import com.rapi.pocketwise.getDeviceManager
import com.rapi.pocketwise.getPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PocketWiseViewModel(
    private val repository: FinanceRepository
) {
    private val _uiState = MutableStateFlow(PocketWiseUiState())
    val uiState: StateFlow<PocketWiseUiState> = _uiState.asStateFlow()

    private val viewModelScope = CoroutineScope(Dispatchers.Main)
    private val deviceManager = getDeviceManager()
    private val platform = getPlatform()

    init {
        viewModelScope.launch {
            while (isActive) {
                updateDeviceStatus()
                delay(2000)
            }
        }
    }

    private fun updateDeviceStatus() {
        try {
            val battery = deviceManager.getBatteryInfo()
            val online = deviceManager.isOnline()
            
            _uiState.update { it.copy(
                isOnline = online,
                batteryLevel = "${battery.level}%",
                batteryStatus = if (battery.isCharging) "Charging" else "Discharging",
                deviceModel = platform.model,
                deviceManufacturer = platform.manufacturer,
                osVersion = platform.name
            ) }
        } catch (e: Exception) {}
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun navigateTo(screen: Screen) {
        _uiState.update { it.copy(currentScreen = screen, selectedNote = null, isEditing = false) }
    }

    fun viewNoteDetail(note: Note) {
        _uiState.update { 
            it.copy(
                currentScreen = Screen.Detail, 
                selectedNote = note,
                aiResult = ""
            ) 
        }
    }

    fun startEditing(note: Note?) {
        _uiState.update { 
            it.copy(
                isEditing = true,
                selectedNote = note,
                titleInput = note?.title ?: "",
                descriptionInput = note?.description ?: "",
                contentInput = note?.content ?: "",
                reminderInput = note?.reminder ?: ""
            ) 
        }
    }

    fun onTitleChanged(value: String) = _uiState.update { it.copy(titleInput = value) }
    fun onDescriptionChanged(value: String) = _uiState.update { it.copy(descriptionInput = value) }
    fun onContentChanged(value: String) = _uiState.update { it.copy(contentInput = value) }
    fun onReminderChanged(value: String) = _uiState.update { it.copy(reminderInput = value) }
    fun onLanguageChanged(value: String) = _uiState.update { it.copy(selectedLanguage = value) }

    fun saveNote() {
        val state = _uiState.value
        val newNote = Note(
            id = state.selectedNote?.id ?: (state.notes.size + 1).toString(),
            title = state.titleInput,
            description = state.descriptionInput,
            content = state.contentInput,
            reminder = state.reminderInput,
            isFavorite = state.selectedNote?.isFavorite ?: false,
            date = state.selectedNote?.date ?: "Hari ini"
        )

        val updatedNotes = if (state.selectedNote == null) {
            state.notes + newNote
        } else {
            state.notes.map { if (it.id == newNote.id) newNote else it }
        }

        _uiState.update { 
            it.copy(
                notes = updatedNotes,
                isEditing = false,
                selectedNote = if (state.selectedNote != null) newNote else null,
                titleInput = "",
                descriptionInput = "",
                contentInput = "",
                reminderInput = ""
            ) 
        }
    }

    fun deleteNote(noteId: String) {
        _uiState.update { state ->
            state.copy(notes = state.notes.filter { it.id != noteId })
        }
    }

    fun toggleFavorite(noteId: String) {
        _uiState.update { state ->
            state.copy(notes = state.notes.map { 
                if (it.id == noteId) it.copy(isFavorite = !it.isFavorite) else it 
            })
        }
    }

    fun summarizeNote() {
        val note = _uiState.value.selectedNote ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, aiResult = "") }
            repository.summarizeNote(note)
                .onSuccess { result ->
                    _uiState.update { it.copy(aiResult = result, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun translateNote() {
        val note = _uiState.value.selectedNote ?: return
        val language = _uiState.value.selectedLanguage
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, aiResult = "") }
            repository.translateNote(note, language)
                .onSuccess { result ->
                    _uiState.update { it.copy(aiResult = result, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(isDarkMode = enabled) }
    }

    fun setSortOrder(order: SortOrder) {
        _uiState.update { it.copy(sortOrder = order) }
    }
    
    fun cancelEdit() {
        _uiState.update { it.copy(isEditing = false) }
    }
}
