package com.example.notesappnavigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.notesappnavigation.components.MyBottomBar
import com.example.notesappnavigation.navigation.*
import com.example.notesappnavigation.screens.*
import com.example.notesappnavigation.database.*
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val noteViewModel: NoteViewModel = koinViewModel()
    val settingsDataStore: SettingsDataStore = koinInject()
    
    val notes by noteViewModel.notes.collectAsState()
    val favoriteNotes by noteViewModel.favoriteNotes.collectAsState()
    val isLoading by noteViewModel.isLoading.collectAsState()
    val searchQuery by noteViewModel.searchQuery.collectAsState()
    val isDarkMode by settingsDataStore.isDarkMode.collectAsState(initial = false)
    val sortOrder by settingsDataStore.sortOrder.collectAsState(initial = "newest")
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "MY NOTES",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = { MyBottomBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddNote.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Notes.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(BottomNavItem.Notes.route) {
                NoteListScreen(
                    notes = if (sortOrder == "newest") notes else notes.reversed(),
                    isLoading = isLoading,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { noteViewModel.updateSearchQuery(it) },
                    onNoteClick = { id -> navController.navigate(Screen.NoteDetail.createRoute(id.toInt())) },
                    onDeleteNote = { id -> noteViewModel.deleteNote(id) },
                    onToggleFavorite = { id, isFav -> noteViewModel.toggleFavorite(id, isFav) }
                )
            }
            
            composable(BottomNavItem.Favorites.route) { 
                FavoritesScreen(
                    favoriteNotes = favoriteNotes,
                    onNoteClick = { id -> navController.navigate(Screen.NoteDetail.createRoute(id.toInt())) },
                    onToggleFavorite = { id, isFav -> noteViewModel.toggleFavorite(id, isFav) }
                )
            }
            
            composable(BottomNavItem.Profile.route) { ProfileScreen() }

            composable(BottomNavItem.Settings.route) {
                SettingsScreen(
                    isDarkMode = isDarkMode,
                    onDarkModeChange = { enabled ->
                        scope.launch { settingsDataStore.setDarkMode(enabled) }
                    },
                    sortOrder = sortOrder,
                    onSortOrderChange = { order ->
                        scope.launch { settingsDataStore.setSortOrder(order) }
                    }
                )
            }

            composable(Screen.AddNote.route) {
                AddNoteScreen(
                    onSave = { title, desc, content, reminder ->
                        noteViewModel.insertNote(title, desc, content, reminder)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                Screen.NoteDetail.route,
                arguments = listOf(navArgument("noteId") { type = NavType.IntType })
            ) { entry ->
                val id = entry.arguments?.getInt("noteId")?.toLong() ?: 0L
                val note = notes.find { it.id == id }
                if (note != null) {
                    NoteDetailScreen(
                        note = note, 
                        onEditClick = { noteId -> navController.navigate(Screen.EditNote.createRoute(noteId.toInt())) },
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            composable(
                Screen.EditNote.route,
                arguments = listOf(navArgument("noteId") { type = NavType.IntType })
            ) { entry ->
                val id = entry.arguments?.getInt("noteId")?.toLong() ?: 0L
                val note = notes.find { it.id == id }
                if (note != null) {
                    EditNoteScreen(
                        note = note,
                        onSave = { noteId, title, desc, content, reminder ->
                            noteViewModel.updateNote(noteId, title, desc, content, reminder)
                            navController.popBackStack()
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
