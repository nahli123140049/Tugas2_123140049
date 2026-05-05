package com.example.notesappnavigation.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.notesappnavigation.database.NoteEntity
import org.junit.Rule
import org.junit.Test

class NoteScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun noteListScreen_displaysNotes() {
        // Arrange
        // Gunakan L untuk angka karena SQLDelight menggunakan Long untuk INTEGER
        val notes = listOf(
            NoteEntity(1L, "Test Note 1", "Desc 1", "Content 1", "Reminder 1", 0L, 0L),
            NoteEntity(2L, "Test Note 2", "Desc 2", "Content 2", "Reminder 2", 1L, 0L)
        )

        // Act
        composeTestRule.setContent {
            NoteListScreen(
                notes = notes,
                isLoading = false,
                isConnected = true,
                searchQuery = "",
                onSearchQueryChange = { _ -> }, // Tambahkan parameter lambda agar tidak error infer type
                onNoteClick = { _ -> },
                onDeleteNote = { _ -> },
                onToggleFavorite = { _, _ -> }
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Test Note 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Note 2").assertIsDisplayed()
    }

    @Test
    fun noteListScreen_showsEmptyMessage_whenNoNotes() {
        // Act
        composeTestRule.setContent {
            NoteListScreen(
                notes = emptyList(),
                isLoading = false,
                isConnected = true,
                searchQuery = "",
                onSearchQueryChange = { _ -> },
                onNoteClick = { _ -> },
                onDeleteNote = { _ -> },
                onToggleFavorite = { _, _ -> }
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Tidak Ada Catatan.").assertIsDisplayed()
    }

    @Test
    fun favoritesScreen_displaysOnlyFavoriteNotes() {
        // Arrange
        val favoriteNotes = listOf(
            NoteEntity(2L, "Favorite Note", "Desc", "Content", "Reminder", 1L, 0L)
        )

        // Act
        composeTestRule.setContent {
            FavoritesScreen(
                favoriteNotes = favoriteNotes,
                onNoteClick = { _ -> },
                onToggleFavorite = { _, _ -> }
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Favorite Note").assertIsDisplayed()
    }
}
