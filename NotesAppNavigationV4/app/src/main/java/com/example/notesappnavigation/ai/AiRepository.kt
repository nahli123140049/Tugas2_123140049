package com.example.notesappnavigation.ai

class AiRepository(private val client: GeminiClient) {

    companion object {
        const val SUMMARIZE_PROMPT =
            "You are an expert note summarizer. Summarize the given note content concisely in 2-3 sentences, preserving the key points. Output only the summary text."

        const val REWRITE_PROMPT =
            "You are a professional writing assistant. Rewrite the given text to be clearer, more concise, and better structured. Maintain the original meaning. Output only the rewritten text."

        const val TRANSLATE_ID_TO_EN_PROMPT =
            "You are a professional translator. Translate the following text from Indonesian to English. Output only the translation."

        const val TRANSLATE_EN_TO_ID_PROMPT =
            "You are a professional translator. Translate the following text from English to Indonesian. Output only the translation."

        const val ASSISTANT_PROMPT =
            "You are a helpful AI assistant for a notes app. You help users brainstorm ideas, organize their thoughts, draft note content, and answer questions related to note-taking. Be concise, helpful, and friendly."
    }

    suspend fun summarize(text: String): Result<String> =
        client.generateContent(
            systemPrompt = SUMMARIZE_PROMPT,
            contents = listOf(GeminiContent(role = "user", parts = listOf(GeminiPart(text))))
        )

    suspend fun rewrite(text: String): Result<String> =
        client.generateContent(
            systemPrompt = REWRITE_PROMPT,
            contents = listOf(GeminiContent(role = "user", parts = listOf(GeminiPart(text))))
        )

    suspend fun translate(text: String): Result<String> =
        client.generateContent(
            systemPrompt = TRANSLATE_ID_TO_EN_PROMPT,
            contents = listOf(GeminiContent(role = "user", parts = listOf(GeminiPart(text))))
        )
}
