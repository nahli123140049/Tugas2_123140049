package com.rapi.pocketwise.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rapi.pocketwise.data.model.Note
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PocketWiseScreen(
    viewModel: PocketWiseViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(
                                "MY NOTES",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF3F51B5)
                    )
                )
                
                val statusColor = if (uiState.isOnline) Color(0xFF4CAF50) else Color(0xFFF44336)
                val statusText = if (uiState.isOnline) "Online" else "Offline"
                val statusIcon = if (uiState.isOnline) Icons.Default.CheckCircle else Icons.Default.Warning

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(statusColor)
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(statusIcon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(statusText, color = Color.White, fontSize = 12.sp)
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = if (uiState.isDarkMode) Color(0xFF121212) else Color.White
            ) {
                NavigationBarItem(
                    selected = uiState.currentScreen == Screen.Notes,
                    onClick = { viewModel.navigateTo(Screen.Notes) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Notes") },
                    label = { Text("Notes") }
                )
                NavigationBarItem(
                    selected = uiState.currentScreen == Screen.Favorites,
                    onClick = { viewModel.navigateTo(Screen.Favorites) },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
                    label = { Text("Favorites") }
                )
                NavigationBarItem(
                    selected = uiState.currentScreen == Screen.Profile,
                    onClick = { viewModel.navigateTo(Screen.Profile) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") }
                )
                NavigationBarItem(
                    selected = uiState.currentScreen == Screen.Settings,
                    onClick = { viewModel.navigateTo(Screen.Settings) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") }
                )
            }
        },
        floatingActionButton = {
            if (uiState.currentScreen == Screen.Notes || uiState.currentScreen == Screen.Favorites) {
                FloatingActionButton(
                    onClick = { viewModel.startEditing(null) },
                    containerColor = Color(0xFFC5CAE9)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Note")
                }
            }
        },
        containerColor = if (uiState.isDarkMode) Color(0xFF121212) else Color(0xFFF5F5F5)
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (uiState.currentScreen) {
                Screen.Notes -> NotesListScreen(uiState, viewModel)
                Screen.Favorites -> FavoritesListScreen(uiState, viewModel)
                Screen.Profile -> ProfileScreen(uiState)
                Screen.Settings -> SettingsScreen(uiState, viewModel)
                Screen.Detail -> NoteDetailScreen(uiState, viewModel)
            }

            if (uiState.isEditing) {
                EditNoteDialog(uiState, viewModel)
            }
        }
    }
}

@Composable
fun NotesListScreen(uiState: PocketWiseUiState, viewModel: PocketWiseViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = viewModel::onSearchQueryChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Cari catatan...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = if (uiState.isDarkMode) Color(0xFF1E1E1E) else Color.White,
                unfocusedContainerColor = if (uiState.isDarkMode) Color(0xFF1E1E1E) else Color.White,
                focusedTextColor = if (uiState.isDarkMode) Color.White else Color.Black,
                unfocusedTextColor = if (uiState.isDarkMode) Color.White else Color.Black
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        val filteredNotes = uiState.notes.filter { 
            it.title.contains(uiState.searchQuery, ignoreCase = true) || 
            it.description.contains(uiState.searchQuery, ignoreCase = true)
        }.let {
            if (uiState.sortOrder == SortOrder.NEWEST) it.reversed() else it
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(filteredNotes) { note ->
                NoteItem(note, viewModel, uiState.isDarkMode)
            }
        }
    }
}

@Composable
fun FavoritesListScreen(uiState: PocketWiseUiState, viewModel: PocketWiseViewModel) {
    val favoriteNotes = uiState.notes.filter { it.isFavorite }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Favorites", style = MaterialTheme.typography.headlineMedium, color = if (uiState.isDarkMode) Color.White else Color.Black)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(favoriteNotes) { note ->
                NoteItem(note, viewModel, uiState.isDarkMode)
            }
        }
    }
}

@Composable
fun NoteItem(note: Note, viewModel: PocketWiseViewModel, isDarkMode: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { viewModel.viewNoteDetail(note) },
        colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(note.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = if (isDarkMode) Color.White else Color.Black)
                Text(note.description, fontSize = 14.sp, color = Color.Gray)
            }
            IconButton(onClick = { viewModel.toggleFavorite(note.id) }) {
                Icon(
                    if (note.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (note.isFavorite) Color.Red else Color.Gray
                )
            }
            IconButton(onClick = { viewModel.deleteNote(note.id) }) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
            }
        }
    }
}

@Composable
fun ProfileScreen(uiState: PocketWiseUiState) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Profile", 
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.headlineMedium, 
            color = if (uiState.isDarkMode) Color.White else Color.Black,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(40.dp))
        Surface(
            modifier = Modifier.size(150.dp),
            shape = CircleShape,
            color = Color(0xFF7986CB)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Person, 
                    contentDescription = null, 
                    modifier = Modifier.size(80.dp),
                    tint = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Nahli Saud Ramdani", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = if (uiState.isDarkMode) Color.White else Color.Black)
        Text("Student / Developer", fontSize = 16.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color.Gray, thickness = 0.5.dp, modifier = Modifier.width(200.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("NIM: 123140049", color = if (uiState.isDarkMode) Color.White else Color.Black)
        }
    }
}

@Composable
fun SettingsScreen(uiState: PocketWiseUiState, viewModel: PocketWiseViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium, color = if (uiState.isDarkMode) Color.White else Color.Black, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Dark Mode", color = if (uiState.isDarkMode) Color.White else Color.Black)
            Switch(checked = uiState.isDarkMode, onCheckedChange = viewModel::toggleDarkMode)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = Color.Gray, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Sort Order", color = if (uiState.isDarkMode) Color.White else Color.Black, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = uiState.sortOrder == SortOrder.NEWEST, onClick = { viewModel.setSortOrder(SortOrder.NEWEST) })
            Text("Newest First", color = if (uiState.isDarkMode) Color.White else Color.Black)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = uiState.sortOrder == SortOrder.OLDEST, onClick = { viewModel.setSortOrder(SortOrder.OLDEST) })
            Text("Oldest First", color = if (uiState.isDarkMode) Color.White else Color.Black)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Device Info", color = Color(0xFF7986CB), fontWeight = FontWeight.Bold)
        DeviceInfoRow("Model", uiState.deviceModel, uiState.isDarkMode)
        DeviceInfoRow("Manufacturer", uiState.deviceManufacturer, uiState.isDarkMode)
        DeviceInfoRow("OS Version", uiState.osVersion, uiState.isDarkMode)
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("Battery Info (Bonus)", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
        DeviceInfoRow("Level", uiState.batteryLevel, uiState.isDarkMode)
        DeviceInfoRow("Status", uiState.batteryStatus, uiState.isDarkMode)
    }
}

@Composable
fun DeviceInfoRow(label: String, value: String, isDarkMode: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray)
        Text(value, color = if (isDarkMode) Color.White else Color.Black, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun NoteDetailScreen(uiState: PocketWiseUiState, viewModel: PocketWiseViewModel) {
    val note = uiState.selectedNote ?: return
    var showLanguageDropdown by remember { mutableStateOf(false) }
    val languages = listOf("English", "Japanese", "Korean", "Arabic", "French", "German", "Indonesian")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text(
            "← Kembali", 
            color = Color(0xFF3F51B5), 
            modifier = Modifier.clickable { viewModel.navigateTo(Screen.Notes) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = if (uiState.isDarkMode) Color(0xFF1E1E1E) else Color.White),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Text(note.title.uppercase(), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f), color = if (uiState.isDarkMode) Color.White else Color.Black)
                    Surface(shape = CircleShape, color = Color(0xFFE8EAF6), modifier = Modifier.size(40.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color(0xFF3F51B5))
                        }
                    }
                }
                
                if (note.reminder.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reminder: ${note.reminder}", color = Color.Gray, fontSize = 14.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Text(note.content, fontSize = 16.sp, lineHeight = 24.sp, color = if (uiState.isDarkMode) Color.LightGray else Color.DarkGray)
                
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { viewModel.startEditing(note) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Edit Catatan", color = Color.White)
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Fitur AI Gemini", fontWeight = FontWeight.Bold, color = if (uiState.isDarkMode) Color.White else Color.Black)
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { viewModel.summarizeNote() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !uiState.isLoading
                    ) {
                        Text("Summarize", fontSize = 12.sp)
                    }
                    
                    Box(modifier = Modifier.weight(1f)) {
                        Button(
                            onClick = { viewModel.translateNote() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !uiState.isLoading
                        ) {
                            Text("Translate", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                
                // Language Selector
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { showLanguageDropdown = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Ke Bahasa: ${uiState.selectedLanguage}")
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = showLanguageDropdown,
                        onDismissRequest = { showLanguageDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        languages.forEach { lang ->
                            DropdownMenuItem(
                                text = { Text(lang) },
                                onClick = {
                                    viewModel.onLanguageChanged(lang)
                                    showLanguageDropdown = false
                                }
                            )
                        }
                    }
                }

                if (uiState.isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                if (uiState.aiResult.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Hasil AI", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(uiState.aiResult, color = if (uiState.isDarkMode) Color.White else Color.Black, fontSize = 14.sp)
                        }
                    }
                }
                
                uiState.errorMessage?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(it, color = Color.Red, fontSize = 14.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteDialog(uiState: PocketWiseUiState, viewModel: PocketWiseViewModel) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.currentSystemDefault()).date
                        viewModel.onReminderChanged(date.toString())
                        showDatePicker = false
                        showTimePicker = true
                    }
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val time = "${timePickerState.hour.toString().padStart(2, '0')}:${timePickerState.minute.toString().padStart(2, '0')}"
                    viewModel.onReminderChanged("${uiState.reminderInput}, $time")
                    showTimePicker = false
                }) { Text("OK") }
            },
            title = { Text("Pilih Waktu") },
            text = { 
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(state = timePickerState) 
                }
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = if (uiState.isDarkMode) Color(0xFF121212) else Color.White
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
            Text(
                if (uiState.selectedNote == null) "Tambah Catatan" else "Edit Catatan",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = if (uiState.isDarkMode) Color.White else Color.Black
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            EditField("Judul", uiState.titleInput, viewModel::onTitleChanged, uiState.isDarkMode)
            EditField("Deskripsi", uiState.descriptionInput, viewModel::onDescriptionChanged, uiState.isDarkMode)
            
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Text("Reminder (Klik untuk pilih waktu)", fontSize = 14.sp, color = if (uiState.isDarkMode) Color.Gray else Color.DarkGray)
                OutlinedTextField(
                    value = uiState.reminderInput,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                    enabled = false,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = if (uiState.isDarkMode) Color.White else Color.Black,
                        disabledBorderColor = Color.Gray,
                        disabledLabelColor = Color.Gray
                    )
                )
            }

            EditField("Isi", uiState.contentInput, viewModel::onContentChanged, uiState.isDarkMode, minLines = 10)
            
            Spacer(modifier = Modifier.weight(1f))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = viewModel::cancelEdit,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Batal")
                }
                Button(
                    onClick = viewModel::saveNote,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Simpan")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun EditField(label: String, value: String, onValueChange: (String) -> Unit, isDarkMode: Boolean, minLines: Int = 1) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, fontSize = 14.sp, color = if (isDarkMode) Color.Gray else Color.DarkGray)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            minLines = minLines,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = if (isDarkMode) Color.White else Color.Black,
                unfocusedTextColor = if (isDarkMode) Color.White else Color.Black,
                focusedBorderColor = Color(0xFF3F51B5),
                unfocusedBorderColor = Color.Gray
            )
        )
    }
}
