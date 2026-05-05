package com.example.notesappnavigation.database

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NoteRepositoryTest {

    private lateinit var repository: NoteRepository
    private lateinit var database: NoteDatabase

    @Before
    fun setup() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        NoteDatabase.Schema.create(driver)
        database = NoteDatabase(driver)
        repository = NoteRepository(database)
    }

    @Test
    fun `insertNote should add note to database`() = runTest {
        // Arrange
        val title = "Test Title"
        val desc = "Test Description"
        val content = "Test Content"
        val reminder = "Test Reminder"

        // Act
        repository.insertNote(title, desc, content, reminder)

        // Assert
        repository.getAllNotes().test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            assertEquals(title, notes[0].title)
            assertEquals(desc, notes[0].description)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteNote should remove note from database`() = runTest {
        // Arrange
        repository.insertNote("Title", "Desc", "Content", "Reminder")
        val notesBefore = repository.getNoteById(1L)
        assertTrue(notesBefore != null)

        // Act
        repository.deleteNote(1L)

        // Assert
        val notesAfter = repository.getNoteById(1L)
        assertTrue(notesAfter == null)
    }

    @Test
    fun `updateFavorite should change favorite status`() = runTest {
        // Arrange
        repository.insertNote("Title", "Desc", "Content", "Reminder")
        
        // Act
        repository.updateFavorite(1L, true)

        // Assert
        val note = repository.getNoteById(1L)
        assertEquals(1L, note?.isFavorite)

        // Act - toggle back
        repository.updateFavorite(1L, false)
        val note2 = repository.getNoteById(1L)
        assertEquals(0L, note2?.isFavorite)
    }

    @Test
    fun `searchNotes should return matching notes`() = runTest {
        // Arrange
        repository.insertNote("Apple", "Desc", "Content", "Reminder")
        repository.insertNote("Banana", "Desc", "Content", "Reminder")

        // Act & Assert
        repository.searchNotes("Apple").test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Apple", result[0].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getFavoriteNotes should only return favorite notes`() = runTest {
        // Arrange
        repository.insertNote("Fav", "Desc", "Content", "Reminder")
        repository.insertNote("Not Fav", "Desc", "Content", "Reminder")
        repository.updateFavorite(1L, true)

        // Act & Assert
        repository.getFavoriteNotes().test {
            val favs = awaitItem()
            assertEquals(1, favs.size)
            assertEquals("Fav", favs[0].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateNote should modify existing note`() = runTest {
        // Arrange
        repository.insertNote("Old Title", "Old Desc", "Old Content", "Old Reminder")
        val id = 1L

        // Act
        repository.updateNote(id, "New Title", "New Desc", "New Content", "New Reminder")

        // Assert
        val updatedNote = repository.getNoteById(id)
        assertEquals("New Title", updatedNote?.title)
        assertEquals("New Desc", updatedNote?.description)
        assertEquals("New Content", updatedNote?.content)
    }

    @Test
    fun `searchNotes with no match should return empty list`() = runTest {
        // Arrange
        repository.insertNote("Title", "Desc", "Content", "Reminder")

        // Act & Assert
        repository.searchNotes("NonExistent").test {
            val result = awaitItem()
            assertTrue(result.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
