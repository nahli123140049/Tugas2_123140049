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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rapi.pocketwise.data.model.Note

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PocketWiseScreen(viewModel: PocketWiseViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "MY NOTES",
                            fontWeight = FontWeight.ExtraBold,
                            color = if (uiState.isDarkMode) Color.White else Color(0xFF2E3E5C),
                            letterSpacing = 1.sp
                        )
                    },
                    actions = {
                        if (uiState.currentScreen == Screen.NOTES || uiState.currentScreen == Screen.FAVORITES) {
                            IconButton(onClick = { viewModel.navigateTo(Screen.EDIT_NOTE) }) {
                                Icon(
                                    Icons.Default.Add, 
                                    contentDescription = "Add Note",
                                    tint = if (uiState.isDarkMode) Color.White else Color(0xFF2E3E5C)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = if (uiState.isDarkMode) Color(0xFF121212) else Color(0xFFD6E2FF)
                    )
                )
                ConnectivityBanner(uiState.isOnline)
            }
        },
        bottomBar = {
            if (uiState.currentScreen != Screen.EDIT_NOTE && uiState.currentScreen != Screen.DETAIL_NOTE) {
                BottomNavigationBar(uiState.currentScreen) { viewModel.navigateTo(it) }
            }
        },
        floatingActionButton = {
            if (uiState.currentScreen == Screen.NOTES || uiState.currentScreen == Screen.FAVORITES) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.navigateTo(Screen.EDIT_NOTE) },
                    containerColor = Color(0xFF5D6D9A),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    icon = { Icon(Icons.Default.Add, "Add") },
                    text = { Text("Tambah Catatan") }
                )
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            color = if (uiState.isDarkMode) Color(0xFF121212) else Color(0xFFF5F7FA)
        ) {
            Box {
                Column {
                    if (uiState.errorMessage != null) {
                        Surface(
                            color = Color.Red.copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = uiState.errorMessage ?: "",
                                color = Color.Red,
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    when (uiState.currentScreen) {
                        Screen.NOTES -> NotesListScreen(uiState, viewModel, false)
                        Screen.FAVORITES -> NotesListScreen(uiState, viewModel, true)
                        Screen.PROFILE -> ProfileScreen(uiState)
                        Screen.SETTINGS -> SettingsScreen(uiState, viewModel)
                        Screen.EDIT_NOTE -> EditNoteScreen(uiState, viewModel)
                        Screen.DETAIL_NOTE -> DetailNoteScreen(uiState, viewModel)
                    }
                }

                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable(enabled = false) {},
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (uiState.isDarkMode) Color(0xFF1E1E1E) else Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = Color(0xFF5D6D9A))
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    "AI Sedang Bekerja...", 
                                    color = if (uiState.isDarkMode) Color.White else Color(0xFF121212),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConnectivityBanner(isOnline: Boolean) {
    val backgroundColor = if (isOnline) Color(0xFF4CAF50) else Color(0xFFF44336)
    val text = if (isOnline) "Online" else "Offline"
    val icon = if (isOnline) Icons.Default.CheckCircle else Icons.Default.Warning

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(text, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BottomNavigationBar(currentScreen: Screen, onNavigate: (Screen) -> Unit) {
    NavigationBar(
        containerColor = Color(0xFF1C1C1E),
        contentColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val navItems = listOf(
            Triple(Screen.NOTES, "Notes", Icons.Default.Home),
            Triple(Screen.FAVORITES, "Favorites", Icons.Default.Favorite),
            Triple(Screen.PROFILE, "Profile", Icons.Default.Person),
            Triple(Screen.SETTINGS, "Settings", Icons.Default.Settings)
        )

        navItems.forEach { (screen, label, icon) ->
            NavigationBarItem(
                selected = currentScreen == screen,
                onClick = { onNavigate(screen) },
                icon = { Icon(icon, null) },
                label = { Text(label, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Color.White,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color(0xFF3A3A3C)
                )
            )
        }
    }
}

@Composable
fun NotesListScreen(uiState: PocketWiseUiState, viewModel: PocketWiseViewModel, favoritesOnly: Boolean) {
    val filteredNotes = uiState.notes.filter { 
        (if (favoritesOnly) it.isFavorite else true) && 
        (it.title.contains(uiState.searchQuery, true) || it.description.contains(uiState.searchQuery, true))
    }.let {
        if (uiState.sortNewest) it.reversed() else it
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = viewModel::onSearchQueryChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Cari catatan...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
            shape = RoundedCornerShape(30.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Gray,
                focusedBorderColor = Color.White,
                unfocusedContainerColor = if (uiState.isDarkMode) Color(0xFF1C1C1E) else Color.White,
                focusedContainerColor = if (uiState.isDarkMode) Color(0xFF1C1C1E) else Color.White
            )
        )

        Spacer(Modifier.height(20.dp))

        if (filteredNotes.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    if (favoritesOnly) "Belum ada favorit" else "Catatan masih kosong.\nKlik + untuk menambah.",
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredNotes) { note ->
                    NoteItem(note, viewModel, uiState.isDarkMode)
                }
            }
        }
    }
}

@Composable
fun NoteItem(note: Note, viewModel: PocketWiseViewModel, isDarkMode: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { viewModel.navigateTo(Screen.DETAIL_NOTE, note) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) Color(0xFF1C1C1E) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    note.title, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 18.sp, 
                    color = if (isDarkMode) Color.White else Color.Black
                )
                Text(
                    note.description, 
                    fontSize = 13.sp, 
                    color = Color.Gray,
                    maxLines = 1
                )
            }
            IconButton(onClick = { viewModel.toggleFavorite(note) }) {
                Icon(
                    if (note.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint = if (note.isFavorite) Color.Red else Color.Gray
                )
            }
            IconButton(onClick = { viewModel.deleteNote(note) }) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(uiState: PocketWiseUiState, viewModel: PocketWiseViewModel) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showTranslateMenu by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        viewModel.onReminderChanged("Selected Date")
                    }
                    showDatePicker = false
                    showTimePicker = true
                }) { Text("OK") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val time = "${timePickerState.hour.toString().padStart(2, '0')}:${timePickerState.minute.toString().padStart(2, '0')}"
                    viewModel.onReminderChanged("30 April 2024, $time") 
                    showTimePicker = false
                }) { Text("OK") }
            },
            title = { Text("Pilih Waktu") },
            text = { TimePicker(state = timePickerState) }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (uiState.selectedNote == null) "Tambah Catatan" else "Edit Catatan", 
                fontSize = 24.sp, 
                fontWeight = FontWeight.Bold,
                color = if (uiState.isDarkMode) Color.White else Color.Black,
                modifier = Modifier.weight(1f)
            )
            
            Box {
                IconButton(onClick = { showTranslateMenu = true }) {
                    Icon(
                        Icons.Default.Translate, 
                        contentDescription = "Translate AI", 
                        tint = if (uiState.isDarkMode) Color.White else Color(0xFF5D6D9A)
                    )
                }
                DropdownMenu(
                    expanded = showTranslateMenu, 
                    onDismissRequest = { showTranslateMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Translate to English") }, 
                        onClick = { 
                            viewModel.translateNoteContent("English")
                            showTranslateMenu = false
                        },
                        leadingIcon = { Icon(Icons.Default.Language, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Translate to Indonesia") }, 
                        onClick = { 
                            viewModel.translateNoteContent("Indonesia")
                            showTranslateMenu = false
                        },
                        leadingIcon = { Icon(Icons.Default.Language, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Translate to Japanese") }, 
                        onClick = { 
                            viewModel.translateNoteContent("Japanese")
                            showTranslateMenu = false
                        },
                        leadingIcon = { Icon(Icons.Default.Language, null) }
                    )
                }
            }
        }
        
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = uiState.noteTitleInput,
            onValueChange = viewModel::onTitleChanged,
            label = { Text("Judul") },
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.noteTitleInput.isBlank() && uiState.errorMessage != null
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.noteDescInput,
            onValueChange = viewModel::onDescChanged,
            label = { Text("Deskripsi Singkat") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        
        Box(modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }) {
            OutlinedTextField(
                value = uiState.noteReminderInput,
                onValueChange = {},
                label = { Text("Reminder (Klik untuk pilih waktu)") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = if (uiState.isDarkMode) Color.White else Color.Black,
                    disabledBorderColor = Color.Gray,
                    disabledLabelColor = Color.Gray,
                    disabledTrailingIconColor = Color.Gray
                ),
                trailingIcon = { Icon(Icons.Default.DateRange, null) }
            )
        }
        
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.noteContentInput,
            onValueChange = viewModel::onContentChanged,
            label = { Text("Isi Catatan") },
            modifier = Modifier.fillMaxWidth().weight(1f)
        )

        Spacer(Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { viewModel.navigateTo(Screen.NOTES) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                shape = RoundedCornerShape(30.dp)
            ) { Text("Batal") }
            Button(
                onClick = { viewModel.saveNote() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D6D9A)),
                shape = RoundedCornerShape(30.dp)
            ) { Text("Simpan") }
        }
    }
}

@Composable
fun DetailNoteScreen(uiState: PocketWiseUiState, viewModel: PocketWiseViewModel) {
    val note = uiState.selectedNote ?: return

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.clickable { viewModel.navigateTo(Screen.NOTES) }.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ArrowBack, null, tint = Color(0xFF5D6D9A), modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Kembali", color = Color(0xFF5D6D9A), fontWeight = FontWeight.Medium)
        }

        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.isDarkMode) Color(0xFF1C1C1E) else Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        note.title.uppercase(),
                        modifier = Modifier.weight(1f),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 32.sp,
                        color = if (uiState.isDarkMode) Color.White else Color.Black
                    )
                    Box(
                        modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFFD6E2FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Face, null, modifier = Modifier.size(20.dp), tint = Color(0xFF2E3E5C))
                    }
                }
                
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(Modifier.width(6.dp))
                    Text("Reminder: ${note.reminder}", color = Color.Gray, fontSize = 14.sp)
                }

                Spacer(Modifier.height(24.dp))
                Text(
                    note.content, 
                    fontSize = 16.sp, 
                    lineHeight = 24.sp,
                    color = if (uiState.isDarkMode) Color(0xFFE0E0E0) else Color(0xFF333333)
                )

                Spacer(Modifier.height(40.dp))
                Button(
                    onClick = { viewModel.navigateTo(Screen.EDIT_NOTE, note) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D6D9A)),
                    shape = RoundedCornerShape(30.dp),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Text("Edit Catatan", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(uiState: PocketWiseUiState) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Profile",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Start,
            color = if (uiState.isDarkMode) Color.White else Color.Black
        )
        Spacer(Modifier.height(50.dp))
        
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(Color(0xFF5D6D9A)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, null, modifier = Modifier.size(100.dp), tint = Color.White)
        }

        Spacer(Modifier.height(30.dp))
        Text(
            "Nahli Saud Ramdani", 
            fontSize = 24.sp, 
            fontWeight = FontWeight.Bold,
            color = if (uiState.isDarkMode) Color.White else Color.Black
        )
        Text("Student / Developer", fontSize = 16.sp, color = Color.Gray)

        Spacer(Modifier.height(40.dp))
        HorizontalDivider(
            color = Color.Gray.copy(alpha = 0.3f), 
            thickness = 1.dp, 
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        
        Spacer(Modifier.height(20.dp))
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.isDarkMode) Color(0xFF1C1C1E) else Color(0xFFD6E2FF)
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp), 
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Info, 
                    null, 
                    tint = if (uiState.isDarkMode) Color.Gray else Color(0xFF2E3E5C), 
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "NIM: 123140049", 
                    color = if (uiState.isDarkMode) Color.White else Color(0xFF2E3E5C), 
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun SettingsScreen(uiState: PocketWiseUiState, viewModel: PocketWiseViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())
    ) {
        Text(
            "Settings", 
            fontSize = 32.sp, 
            fontWeight = FontWeight.ExtraBold,
            color = if (uiState.isDarkMode) Color.White else Color.Black
        )
        Spacer(Modifier.height(30.dp))

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(
                "Dark Mode", 
                modifier = Modifier.weight(1f), 
                fontSize = 18.sp, 
                fontWeight = FontWeight.Medium,
                color = if (uiState.isDarkMode) Color.White else Color.Black
            )
            Switch(checked = uiState.isDarkMode, onCheckedChange = viewModel::toggleDarkMode)
        }

        Spacer(Modifier.height(20.dp))
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
        Spacer(Modifier.height(20.dp))

        Text(
            "Sort Order", 
            fontSize = 20.sp, 
            fontWeight = FontWeight.Bold,
            color = if (uiState.isDarkMode) Color.White else Color.Black
        )
        Spacer(Modifier.height(12.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically, 
            modifier = Modifier.clickable { viewModel.setSortOrder(true) }
        ) {
            RadioButton(selected = uiState.sortNewest, onClick = { viewModel.setSortOrder(true) })
            Text(
                "Newest First", 
                fontSize = 16.sp,
                color = if (uiState.isDarkMode) Color.White else Color.Black
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically, 
            modifier = Modifier.clickable { viewModel.setSortOrder(false) }
        ) {
            RadioButton(selected = !uiState.sortNewest, onClick = { viewModel.setSortOrder(false) })
            Text(
                "Oldest First", 
                fontSize = 16.sp,
                color = if (uiState.isDarkMode) Color.White else Color.Black
            )
        }

        Spacer(Modifier.height(30.dp))
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
        Spacer(Modifier.height(30.dp))

        Text("Device Info", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5D6D9A))
        Spacer(Modifier.height(10.dp))
        InfoRow("Model", uiState.deviceModel, uiState.isDarkMode)
        InfoRow("Manufacturer", uiState.deviceManufacturer, uiState.isDarkMode)
        InfoRow("OS Version", uiState.osVersion, uiState.isDarkMode)

        Spacer(Modifier.height(24.dp))
        Text("Battery Info (Bonus)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
        Spacer(Modifier.height(10.dp))
        InfoRow("Level", "${uiState.batteryLevel}%", uiState.isDarkMode)
        InfoRow("Status", uiState.batteryStatus, uiState.isDarkMode)
        
        Spacer(Modifier.height(16.dp))
        OutlinedButton(
            onClick = viewModel::refreshBatteryInfo,
            modifier = Modifier.align(Alignment.End),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text("Update Status")
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, isDarkMode: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(label, modifier = Modifier.weight(1f), color = Color.Gray, fontSize = 15.sp)
        Text(
            value, 
            fontWeight = FontWeight.Bold, 
            fontSize = 15.sp,
            color = if (isDarkMode) Color.White else Color.Black
        )
    }
}
