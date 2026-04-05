package com.example.notesappnavigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun MainScreen() {
    // 1. Setup NavController buat navigasi antar layar [cite: 83, 145]
    val navController = rememberNavController()

    // Daftar item buat Bottom Navigation [cite: 281, 603]
    val bottomItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Favorites,
        BottomNavItem.Profile
    )

    // 2. Scaffold sebagai struktur dasar UI (punya slot buat bottomBar) [cite: 310, 315]
    Scaffold(
        bottomBar = {
            NavigationBar {
                // Ambil route saat ini buat nentuin tab mana yang aktif [cite: 296, 297]
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                bottomItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute == item.route, // Cek status aktif [cite: 300]
                        onClick = {
                            // Pindah tab dengan proteksi backstack [cite: 302, 303]
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true // Hindari duplicate screen [cite: 194]
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        // 3. NavHost: Container yang nampilin layar berdasarkan route [cite: 79, 82]
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route, // Route awal [cite: 88]
            modifier = Modifier.padding(paddingValues) // Biar konten gak ketutup BottomBar [cite: 322]
        ) {
            // --- TAB NAVIGATION DESTINATIONS ---

            // Route Home (Note List) [cite: 154, 324]
            composable(BottomNavItem.Home.route) {
                NoteListScreen(onNoteClick = { id ->
                    // Navigasi ke Detail dengan Argument [cite: 160, 327]
                    navController.navigate(Screen.NoteDetail.createRoute(id))
                })
            }

            // Route Favorites [cite: 329]
            composable(BottomNavItem.Favorites.route) {
                FavoritesScreen()
            }

            // Route Profile [cite: 329]
            composable(BottomNavItem.Profile.route) {
                ProfileScreen()
            }

            // --- NON-TAB DESTINATIONS (DETAIL SCREENS) ---

            // Route Note Detail dengan argument ID (Latihan 2) [cite: 219, 330]
            composable(
                route = Screen.NoteDetail.route, // Alamatnya: "note_detail/{noteId}" [cite: 216]
                arguments = listOf(
                    navArgument("noteId") { type = NavType.IntType } // Setup tipe data [cite: 221]
                )
            ) { backStackEntry ->
                // Ambil angka ID dari arguments [cite: 223, 331]
                val id = backStackEntry.arguments?.getInt("noteId") ?: 0
                NoteDetailScreen(
                    noteId = id,
                    onBack = { navController.popBackStack() } // Navigasi balik [cite: 169]
                )
            }
        }
    }
}