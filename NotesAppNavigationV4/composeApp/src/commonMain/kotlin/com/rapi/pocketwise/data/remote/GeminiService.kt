package com.rapi.pocketwise.data.remote

import com.rapi.pocketwise.data.model.GeminiContent
import com.rapi.pocketwise.data.model.GeminiPart
import com.rapi.pocketwise.data.model.GeminiRequest
import com.rapi.pocketwise.data.model.GeminiResponse
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestTimeoutException
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
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 30_000
        }
    }

    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta"
    private val model = "gemini-2.5-flash"

    suspend fun generateContent(prompt: String): Result<String> {
        return try {
            if (apiKey.isBlank() || !apiKey.startsWith("AIza")) {
                return Result.failure(
                    Exception("Gemini API key tidak valid atau belum terbaca dari BuildConfig.")
                )
            }

            if (prompt.isBlank()) {
                return Result.failure(
                    Exception("Prompt tidak boleh kosong.")
                )
            }

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        role = "user",
                        parts = listOf(
                            GeminiPart(text = prompt)
                        )
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

            println("GEMINI_HTTP_STATUS: $statusCode")
            println("GEMINI_RAW_RESPONSE: $rawResponse")

            if (statusCode == 400) {
                return Result.failure(
                    Exception(
                        "Request Gemini tidak valid atau API key tidak terbaca. Detail: $rawResponse"
                    )
                )
            }

            if (statusCode == 404) {
                return Result.failure(
                    Exception("Model Gemini tidak ditemukan. Cek nama model di GeminiService.kt.")
                )
            }

            if (statusCode == 429) {
                return Result.failure(
                    Exception(
                        "Quota Gemini API tercapai atau free tier project tidak aktif. Detail: $rawResponse"
                    )
                )
            }

            if (statusCode !in 200..299) {
                return Result.failure(
                    Exception("Gemini API error $statusCode: $rawResponse")
                )
            }

            val response = json.decodeFromString<GeminiResponse>(rawResponse)

            val blockReason = response.promptFeedback?.blockReason

            if (!blockReason.isNullOrBlank()) {
                return Result.failure(
                    Exception("Prompt diblokir oleh Gemini. Alasan: $blockReason")
                )
            }

            val candidate = response.candidates.firstOrNull()

            if (candidate == null) {
                return Result.failure(
                    Exception("Gemini tidak mengembalikan kandidat jawaban. Raw response: $rawResponse")
                )
            }

            if (!candidate.finishReason.isNullOrBlank() && candidate.finishReason != "STOP") {
                return Result.failure(
                    Exception("Gemini menghentikan respons. Alasan: ${candidate.finishReason}")
                )
            }

            val resultText = candidate
                .content
                ?.parts
                ?.mapNotNull { part ->
                    part.text
                }
                ?.joinToString(separator = "\n")
                ?.trim()

            if (resultText.isNullOrBlank()) {
                Result.failure(
                    Exception("Gemini tidak memberikan teks respons. Raw response: $rawResponse")
                )
            } else {
                Result.success(resultText)
            }
        } catch (e: HttpRequestTimeoutException) {
            Result.failure(
                Exception("Request timeout. Periksa koneksi internet.")
            )
        } catch (e: Exception) {
            Result.failure(
                Exception(e.message ?: "Terjadi kesalahan tidak diketahui.")
            )
        }
    }
}