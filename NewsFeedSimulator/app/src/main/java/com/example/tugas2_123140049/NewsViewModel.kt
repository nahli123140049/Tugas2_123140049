package com.example.tugas2_123140049

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {

    private val manager = NewsManager()

    private val _selectedCategory = MutableStateFlow("Tech")
    val selectedCategory: StateFlow<String> = _selectedCategory

    private val _newsList = MutableStateFlow<List<News>>(emptyList())
    val newsList: StateFlow<List<News>> = _newsList

    val readCount: StateFlow<Int> = manager.readCount

    init {
        viewModelScope.launch {
            manager.newsFlow()
                .filter { it.category == _selectedCategory.value }
                .collect { news ->
                    _newsList.value = _newsList.value + news
                    manager.markAsRead()
                }
        }
    }

    fun changeCategory(category: String) {
        _selectedCategory.value = category
        _newsList.value = emptyList()
    }
}
