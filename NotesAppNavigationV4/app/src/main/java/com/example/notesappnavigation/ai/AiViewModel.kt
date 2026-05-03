package com.example.notesappnavigation.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AiViewModel(private val repository: AiRepository) : ViewModel() {

    private val _aiState = MutableStateFlow<AiState<String>>(AiState.Idle)
    val aiState: StateFlow<AiState<String>> = _aiState.asStateFlow()

    fun summarizeNote(content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            _aiState.value = AiState.Loading
            repository.summarize(content)
                .onSuccess { _aiState.value = AiState.Success(it) }
                .onFailure { _aiState.value = AiState.Error(it.message ?: "Unknown error") }
        }
    }

    fun rewriteNote(content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            _aiState.value = AiState.Loading
            repository.rewrite(content)
                .onSuccess { _aiState.value = AiState.Success(it) }
                .onFailure { _aiState.value = AiState.Error(it.message ?: "Unknown error") }
        }
    }

    fun translateNote(content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            _aiState.value = AiState.Loading
            repository.translate(content)
                .onSuccess { _aiState.value = AiState.Success(it) }
                .onFailure { _aiState.value = AiState.Error(it.message ?: "Unknown error") }
        }
    }

    fun clearState() {
        _aiState.value = AiState.Idle
    }
}
