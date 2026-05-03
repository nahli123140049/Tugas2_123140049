package com.example.notesappnavigation.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeminiPart(val text: String)

@Serializable
data class GeminiContent(val role: String, val parts: List<GeminiPart>)

@Serializable
data class GeminiSystemInstruction(val parts: List<GeminiPart>)

@Serializable
data class GeminiRequest(
    @SerialName("system_instruction") val systemInstruction: GeminiSystemInstruction? = null,
    val contents: List<GeminiContent>
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent? = null
)

@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate> = emptyList()
)
