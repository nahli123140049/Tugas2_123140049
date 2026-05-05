package com.rapi.pocketwise.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val content: String = "",
    val reminder: String = "",
    val isFavorite: Boolean = false,
    val createdAt: Long = 0L
)
