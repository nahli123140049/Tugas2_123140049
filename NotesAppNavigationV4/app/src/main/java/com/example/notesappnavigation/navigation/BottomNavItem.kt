package com.example.notesappnavigation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Notes : BottomNavItem("notes_tab", Icons.Default.Home, "Notes")
    object Favorites : BottomNavItem("favorites_tab", Icons.Default.Favorite, "Favorites")
    object Profile : BottomNavItem("profile_tab", Icons.Default.Person, "Profile")
    object Settings : BottomNavItem("settings_tab", Icons.Default.Settings, "Settings")
    object AiAssistant : BottomNavItem("ai_tab", Icons.Default.Star, "AI")
}