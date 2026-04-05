package com.example.notesappnavigation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notesappnavigation.components.NoteItem
import com.example.notesappnavigation.model.Note

@Composable
fun NoteListScreen(notes: List<Note>, favoriteIds: SnapshotStateList<Int>, onNoteClick: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
    ) {
        Text(
            "My Daily Notes",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            color = MaterialTheme.colorScheme.primary
        )
        LazyColumn(
            contentPadding = PaddingValues(bottom = 80.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(notes) { note ->
                NoteItem(
                    note = note,
                    isFavorite = favoriteIds.contains(note.id),
                    onNoteClick = onNoteClick,
                    onFavClick = {
                        if (favoriteIds.contains(note.id)) favoriteIds.remove(note.id) else favoriteIds.add(note.id)
                    }
                )
            }
        }
    }
}

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start).padding(bottom = 32.dp)
        )

        Box(contentAlignment = Alignment.BottomEnd) {
            Surface(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.padding(30.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            SmallFloatingActionButton(
                onClick = { },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                shape = CircleShape,
                modifier = Modifier.offset(x = (-8).dp, y = (-8).dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Profile Picture", modifier = Modifier.size(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Nahli Saud Ramdani", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(text = "Student / Developer", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.5f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "NIM: 123140049", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
fun FavoritesScreen(notes: List<Note>, favoriteIds: List<Int>, onNoteClick: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
    ) {
        Text("Favorite Notes", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(16.dp), color = Color(0xFFE91E63))
        
        val favoriteNotes = notes.filter { favoriteIds.contains(it.id) }
        
        if (favoriteNotes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Text("Belum ada favorit bre.", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(favoriteNotes) { note ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onNoteClick(note.id) },
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 4.dp
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Red)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(note.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoteDetailScreen(note: Note, onEditClick: (Int) -> Unit, onBack: () -> Unit) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), MaterialTheme.colorScheme.surface)
    )

    Column(modifier = Modifier.fillMaxSize().background(gradientBrush).padding(24.dp)) {
        TextButton(onClick = onBack) { Text("← Kembali", color = MaterialTheme.colorScheme.primary) }
        Spacer(modifier = Modifier.height(20.dp))
        ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.elevatedCardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(note.title.uppercase(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.clip(CircleShape).background(Color.Yellow).padding(4.dp)) {
                        Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reminder: ${note.reminder}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("Isi Catatan:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(note.content, style = MaterialTheme.typography.bodyLarge, lineHeight = 24.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { onEditClick(note.id) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Edit Catatan Ini") }
            }
        }
    }
}

@Composable
fun AddNoteScreen(onSave: (String, String, String, String) -> Unit, onBack: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var reminder by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            "Tambah Catatan Baru",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Judul") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Deskripsi Singkat") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = reminder,
            onValueChange = { reminder = it },
            label = { Text("Reminder (Waktu)") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Isi Catatan") },
            modifier = Modifier.fillMaxWidth().weight(1f).padding(bottom = 16.dp)
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                Text("Batal")
            }
            Button(
                onClick = { onSave(title, description, content, reminder) },
                modifier = Modifier.weight(1f),
                enabled = title.isNotBlank() && content.isNotBlank()
            ) {
                Text("Simpan")
            }
        }
    }
}

@Composable
fun EditNoteScreen(note: Note, onSave: (String, String, String, String) -> Unit, onBack: () -> Unit) {
    var title by remember { mutableStateOf(note.title) }
    var description by remember { mutableStateOf(note.description) }
    var content by remember { mutableStateOf(note.content) }
    var reminder by remember { mutableStateOf(note.reminder) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            "Edit Catatan",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Judul") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Deskripsi Singkat") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = reminder,
            onValueChange = { reminder = it },
            label = { Text("Reminder (Waktu)") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Isi Catatan") },
            modifier = Modifier.fillMaxWidth().weight(1f).padding(bottom = 16.dp)
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                Text("Batal")
            }
            Button(
                onClick = { onSave(title, description, content, reminder) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Simpan")
            }
        }
    }
}
