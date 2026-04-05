package com.example.notesappnavigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.notesappnavigation.components.MyBottomBar
import com.example.notesappnavigation.navigation.*
import com.example.notesappnavigation.screens.*
import com.example.notesappnavigation.model.Note

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val favoriteIds = remember { mutableStateListOf<Int>() }
    
    val notes = remember {
        mutableStateListOf(
            Note(1, "Tugas Pemrograman Mobile", "Deadline besok pagi jam 8", "Kerjakan bagian navigasi dan pastikan datanya dinamis sesuai permintaan user.", "08:00 AM"),
            Note(2, "Belanja Bulanan", "Beli kebutuhan dapur", "List belanja: Beras, minyak, telur, dan kopi buat begadang ngoding.", "07:00 PM"),
            Note(3, "Meeting Project", "Diskusi via Zoom", "Persiapkan materi presentasi mengenai arsitektur aplikasi terbaru.", "02:00 PM"),
            Note(4, "Service Motor", "Ke bengkel resmi", "Ganti oli dan cek rem biar aman kalau mau jalan jauh.", "10:00 AM"),
            Note(5, "Olahraga Sore", "Lari di stadion", "Minimal 5 keliling biar badan tetap fit meskipun sibuk ngoding.", "05:00 PM")
        )
    }

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
                NoteListScreen(notes, favoriteIds) { id ->
                    navController.navigate(Screen.NoteDetail.createRoute(id))
                }
            }
            
            composable(BottomNavItem.Favorites.route) { 
                FavoritesScreen(notes, favoriteIds) { id ->
                    navController.navigate(Screen.NoteDetail.createRoute(id))
                }
            }
            
            composable(BottomNavItem.Profile.route) { ProfileScreen() }

            composable(Screen.AddNote.route) {
                AddNoteScreen(
                    onSave = { title, desc, content, reminder ->
                        val newId = if (notes.isEmpty()) 1 else notes.maxOf { it.id } + 1
                        notes.add(Note(newId, title, desc, content, reminder))
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                Screen.NoteDetail.route,
                arguments = listOf(navArgument("noteId") { type = NavType.IntType })
            ) { entry ->
                val id = entry.arguments?.getInt("noteId") ?: 0
                val note = notes.find { it.id == id }
                if (note != null) {
                    NoteDetailScreen(
                        note = note, 
                        onEditClick = { navController.navigate(Screen.EditNote.createRoute(id)) }, 
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            composable(
                Screen.EditNote.route,
                arguments = listOf(navArgument("noteId") { type = NavType.IntType })
            ) { entry ->
                val id = entry.arguments?.getInt("noteId") ?: 0
                val noteIndex = notes.indexOfFirst { it.id == id }
                if (noteIndex != -1) {
                    EditNoteScreen(
                        note = notes[noteIndex],
                        onSave = { title, desc, content, reminder ->
                            notes[noteIndex] = notes[noteIndex].copy(
                                title = title,
                                description = desc,
                                content = content,
                                reminder = reminder
                            )
                            navController.popBackStack()
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
