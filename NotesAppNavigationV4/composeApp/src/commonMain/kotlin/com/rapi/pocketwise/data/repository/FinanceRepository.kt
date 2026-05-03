package com.rapi.pocketwise.data.repository

import com.rapi.pocketwise.data.model.Note

interface FinanceRepository {
    suspend fun summarizeNote(note: Note): Result<String>
    suspend fun translateNote(note: Note, targetLanguage: String): Result<String>
}
