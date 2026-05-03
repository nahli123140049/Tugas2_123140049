package com.rapi.pocketwise.data.repository

import com.rapi.pocketwise.data.model.Note
import com.rapi.pocketwise.data.remote.GeminiService

class FinanceRepositoryImpl(
    private val geminiService: GeminiService
) : FinanceRepository {

    override suspend fun summarizeNote(note: Note): Result<String> {
        if (note.content.isBlank()) {
            return Result.failure(Exception("Konten catatan kosong."))
        }

        val prompt = """
            Tugasmu adalah membuat ringkasan singkat dan padat dari catatan berikut.
            
            Judul: ${note.title}
            Isi:
            ${note.content}
            
            Aturan:
            - Berikan ringkasan dalam bentuk poin-poin.
            - Gunakan Bahasa Indonesia.
            - Fokus pada poin-poin terpenting.
        """.trimIndent()

        return geminiService.generateContent(prompt)
    }

    override suspend fun translateNote(note: Note, targetLanguage: String): Result<String> {
        if (note.content.isBlank()) {
            return Result.failure(Exception("Konten catatan kosong."))
        }

        val prompt = """
            Terjemahkan konten catatan berikut ke dalam Bahasa $targetLanguage.
            
            Judul Asli: ${note.title}
            Konten Asli:
            ${note.content}
            
            Aturan:
            - Terjemahkan judul dan kontennya.
            - Pertahankan makna aslinya.
            - Berikan hasil dalam format:
              Judul: [Hasil Terjemahan Judul]
              Konten: [Hasil Terjemahan Konten]
        """.trimIndent()

        return geminiService.generateContent(prompt)
    }
}
