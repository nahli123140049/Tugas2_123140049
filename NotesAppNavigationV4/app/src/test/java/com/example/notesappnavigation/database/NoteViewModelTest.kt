package com.example.notesappnavigation.database

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NoteViewModelTest {

    private val repository: NoteRepository = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `notes flow should emit values from repository`() = runTest {
        // Arrange
        val mockNotes = listOf(
            NoteEntity(1, "Title", "Desc", "Content", "Reminder", 0, 0)
        )
        every { repository.getAllNotes() } returns flowOf(mockNotes)
        
        val vm = NoteViewModel(repository)

        // Act & Assert
        vm.notes.test {
            // StateIn initial value
            assertEquals(emptyList<NoteEntity>(), awaitItem())
            
            // Advance time to bypass debounce(300L)
            testDispatcher.scheduler.advanceTimeBy(400L)
            
            val result = awaitItem()
            assertEquals(mockNotes, result)
        }
    }

    @Test
    fun `updateSearchQuery should trigger repository search`() = runTest {
        // Arrange
        val query = "search"
        val mockNotes = listOf(NoteEntity(1, "Found", "Desc", "Content", "Reminder", 0, 0))
        every { repository.getAllNotes() } returns flowOf(emptyList())
        every { repository.searchNotes(query) } returns flowOf(mockNotes)
        
        val vm = NoteViewModel(repository)

        // Act & Assert
        vm.notes.test {
            assertEquals(emptyList<NoteEntity>(), awaitItem()) // Initial emission from stateIn
            
            // Wait for initial debounce to finish first
            testDispatcher.scheduler.advanceTimeBy(400L)
            // If it triggered a fetch, it might emit [] again from getAllNotes()
            // depending on implementation, flatMapLatest might emit the result of getAllNotes
            // but let's assume it only emits if it's a new item or if the stream emits.
            
            vm.updateSearchQuery(query)
            testDispatcher.scheduler.advanceTimeBy(400L) // Account for debounce
            
            assertEquals(mockNotes, awaitItem())
        }
    }

    @Test
    fun `insertNote should call repository insert`() = runTest {
        val vm = NoteViewModel(repository)
        val title = "Title"
        val desc = "Desc"
        val content = "Content"
        val reminder = "Reminder"

        vm.insertNote(title, desc, content, reminder)
        testDispatcher.scheduler.runCurrent()

        coVerify { repository.insertNote(title, desc, content, reminder) }
    }

    @Test
    fun `toggleFavorite should call repository updateFavorite with inverted value`() = runTest {
        val vm = NoteViewModel(repository)
        val id = 1L
        val currentIsFavorite = false

        vm.toggleFavorite(id, currentIsFavorite)
        testDispatcher.scheduler.runCurrent()

        coVerify { repository.updateFavorite(id, true) }
    }

    @Test
    fun `deleteNote should call repository delete`() = runTest {
        val vm = NoteViewModel(repository)
        val id = 1L

        vm.deleteNote(id)
        testDispatcher.scheduler.runCurrent()

        coVerify { repository.deleteNote(id) }
    }
}
