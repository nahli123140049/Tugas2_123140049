package com.rapi.pocketwise.data.remote

import com.rapi.pocketwise.data.model.GeminiContent
import com.rapi.pocketwise.data.model.GeminiPart
import com.rapi.pocketwise.data.model.GeminiRequest
import com.rapi.pocketwise.data.model.GeminiResponse
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class GeminiService(
    private val apiKey: String
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 60_000
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 60_000
        }
    }

    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta"
    private val model = "gemini-2.5-flash"

    suspend fun generateContent(prompt: String): Result<String> {
        return try {
            if (apiKey.isBlank() || apiKey == "null" || !apiKey.startsWith("AIza")) {
                return Result.failure(Exception("API Key tidak valid. Silakan Rebuild Project."))
            }

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        role = "user",
                        parts = listOf(GeminiPart(text = prompt))
                    )
                )
            )

            val httpResponse = client.post(
                "$baseUrl/models/$model:generateContent"
            ) {
                contentType(ContentType.Application.Json)
                parameter("key", apiKey)
                setBody(request)
            }

            val rawResponse = httpResponse.bodyAsText()
            val statusCode = httpResponse.status.value

            when (statusCode) {
                200 -> {
                    val response = json.decodeFromString<GeminiResponse>(rawResponse)
                    val resultText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (resultText.isNullOrBlank()) {
                        Result.failure(Exception("Gemini memberikan respons kosong."))
                    } else {
                        Result.success(resultText)
                    }
                }
                503 -> Result.failure(Exception("Server Gemini sedang sibuk. Coba lagi nanti."))
                429 -> Result.failure(Exception("Limit harian API habis."))
                else -> Result.failure(Exception("Error $statusCode: $rawResponse"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Koneksi gagal: ${e.message}"))
        }
    }
}
