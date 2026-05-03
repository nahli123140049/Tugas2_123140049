package com.rapi.NoteAppsNavigationV4.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id: String,
    val title: String,
    val description: String,
    val content: String,
    val reminder: String = "",
    val isFavorite: Boolean = false,
    val date: String
)
