package com.example.notesappnavigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen(onNavigateToDetail: (Int) -> Unit) {
    // Modifier.fillMaxSize() biar Column menuhin layar [cite: 322]
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center, // Tengah secara vertikal [cite: 175]
        horizontalAlignment = Alignment.CenterHorizontally // Tengah secara horizontal [cite: 175]
    ) {
        Text("Home Screen - Daftar Catatan")
        Button(onClick = { onNavigateToDetail(42) }) {
            Text("Lihat Catatan 42")
        }
    }
}

@Composable
fun DetailScreen(noteId: Int, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Detail Screen")
        Text("ID Catatan: $noteId")
        Button(onClick = onBack) {
            Text("Back")
        }
    }
}