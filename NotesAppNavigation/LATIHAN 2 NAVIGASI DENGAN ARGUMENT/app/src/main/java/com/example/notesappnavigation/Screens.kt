package com.example.notesappnavigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NoteListScreen(onNoteClick: (Int) -> Unit) {
    // Anggep aja ini daftar ID catatan lo bre
    val notes = listOf(1, 2, 3, 4, 5)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Daftar Catatan", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(16.dp))

        LazyColumn {
            items(notes) { id ->
                ListItem(
                    headlineContent = { Text("Catatan ke-$id") },
                    modifier = Modifier.clickable { onNoteClick(id) } // navigate dengan noteId
                )
            }
        }
    }
}

@Composable
fun NoteDetailScreen(noteId: Int, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Halaman Detail", style = MaterialTheme.typography.headlineSmall)
        Text("Sedang Membuka catatan dengan ID: $noteId") // Menampilkan noteId
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) { // Back navigation
            Text("Kembali")
        }
    }
}