package com.example.notesappnavigation.ai

sealed class AiState<out T> {
    object Idle : AiState<Nothing>()
    object Loading : AiState<Nothing>()
    data class Success<T>(val data: T) : AiState<T>()
    data class Error(val message: String) : AiState<Nothing>()
}
