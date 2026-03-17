package com.example.tugas2_123140049

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class NewsManager {

    private val categories = listOf("Tech", "Sports", "Finance")

    // Flow berita tiap 2 detik
    fun newsFlow(): Flow<News> = flow {
        var id = 1
        while (true) {
            delay(2000)
            emit(
                News(
                    id = id,
                    title = "Breaking News $id",
                    category = categories.random()
                )
            )
            id++
        }
    }

    // StateFlow untuk jumlah berita dibaca
    private val _readCount = MutableStateFlow(0)
    val readCount: StateFlow<Int> = _readCount

    fun markAsRead() {
        _readCount.value++
    }

    // Async ambil detail berita
    suspend fun fetchDetail(newsId: Int): String {
        delay(1000)
        return "Detail lengkap untuk berita $newsId"
    }
}
