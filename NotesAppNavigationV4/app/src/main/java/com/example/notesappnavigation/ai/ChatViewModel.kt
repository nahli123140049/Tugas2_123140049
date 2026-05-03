package com.example.notesappnavigation.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(val role: String, val text: String)

class ChatViewModel(private val geminiClient: GeminiClient) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isStreaming = MutableStateFlow(false)
    val isStreaming: StateFlow<Boolean> = _isStreaming.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun sendMessage(text: String) {
        val userMessage = ChatMessage("user", text)
        _messages.value = _messages.value + userMessage

        viewModelScope.launch {
            _isStreaming.value = true
            _error.value = null

            val assistantIndex = _messages.value.size
            _messages.value = _messages.value + ChatMessage("model", "")

            val contentsForApi = _messages.value
                .dropLast(1) // exclude the empty placeholder
                .map { msg -> GeminiContent(role = msg.role, parts = listOf(GeminiPart(msg.text))) }

            try {
                var fullText = ""
                geminiClient.streamContent(
                    systemPrompt = AiRepository.ASSISTANT_PROMPT,
                    contents = contentsForApi
                ).collect { chunk ->
                    fullText += chunk
                    val updated = _messages.value.toMutableList()
                    updated[assistantIndex] = ChatMessage("model", fullText)
                    _messages.value = updated
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Something went wrong"
                _messages.value = _messages.value.dropLast(1)
            } finally {
                _isStreaming.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
