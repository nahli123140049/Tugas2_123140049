package com.example.notesappnavigation.ai

import com.example.notesappnavigation.BuildConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class GeminiClient {

    private val json = Json { ignoreUnknownKeys = true }

    private val httpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(json)
        }
        expectSuccess = false
    }

    companion object {
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta"
        private const val MODEL = "gemini-1.5-flash"
        private const val MAX_RETRIES = 3
    }

    private class RetryableHttpException(val statusCode: Int) :
        Exception("HTTP $statusCode – retryable")

    private suspend fun <T> withRetry(block: suspend () -> T): T {
        var attempt = 0
        var delayMs = 1000L
        while (true) {
            try {
                return block()
            } catch (e: RetryableHttpException) {
                if (attempt >= MAX_RETRIES) throw e
            } catch (e: Exception) {
                val isTimeout = e.message?.contains("timeout", ignoreCase = true) == true ||
                        e.message?.contains("timed out", ignoreCase = true) == true
                if (!isTimeout || attempt >= MAX_RETRIES) throw e
            }
            delay(delayMs)
            delayMs *= 2
            attempt++
        }
    }

    suspend fun generateContent(
        systemPrompt: String?,
        contents: List<GeminiContent>
    ): Result<String> {
        return try {
            val text = withRetry { doGenerateContent(systemPrompt, contents) }
            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun doGenerateContent(
        systemPrompt: String?,
        contents: List<GeminiContent>
    ): String {
        val request = GeminiRequest(
            systemInstruction = systemPrompt?.let {
                GeminiSystemInstruction(listOf(GeminiPart(it)))
            },
            contents = contents
        )

        val response: HttpResponse = httpClient.post(
            "$BASE_URL/models/$MODEL:generateContent"
        ) {
            parameter("key", BuildConfig.GEMINI_API_KEY)
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        val statusCode = response.status.value
        if (statusCode == 429 || statusCode in 500..599) {
            throw RetryableHttpException(statusCode)
        }
        if (!response.status.isSuccess()) {
            throw Exception("Gemini API error: ${response.status}")
        }

        val geminiResponse = response.body<GeminiResponse>()
        return geminiResponse.candidates
            .firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: throw Exception("No content in Gemini response")
    }

    fun streamContent(
        systemPrompt: String?,
        contents: List<GeminiContent>
    ): Flow<String> = flow {
        val request = GeminiRequest(
            systemInstruction = systemPrompt?.let {
                GeminiSystemInstruction(listOf(GeminiPart(it)))
            },
            contents = contents
        )

        httpClient.preparePost(
            "$BASE_URL/models/$MODEL:streamGenerateContent"
        ) {
            parameter("key", BuildConfig.GEMINI_API_KEY)
            parameter("alt", "sse")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.execute { response ->
            val channel: ByteReadChannel = response.bodyAsChannel()
            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line() ?: break
                if (line.startsWith("data: ")) {
                    val jsonStr = line.removePrefix("data: ").trim()
                    if (jsonStr == "[DONE]") break
                    try {
                        val chunk = json.decodeFromString<GeminiResponse>(jsonStr)
                        val text = chunk.candidates
                            .firstOrNull()?.content?.parts?.firstOrNull()?.text
                        if (!text.isNullOrEmpty()) emit(text)
                    } catch (_: Exception) {
                        // skip malformed chunk
                    }
                }
            }
        }
    }
}
