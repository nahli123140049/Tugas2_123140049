package com.example.notesappnavigation.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notesappnavigation.database.NoteEntity
import com.example.notesappnavigation.platform.BatteryInfo
import com.example.notesappnavigation.platform.DeviceInfo
import com.example.notesappnavigation.platform.NetworkMonitor
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NoteListScreen(
    notes: List<NoteEntity>,
    isLoading: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onNoteClick: (Long) -> Unit,
    onDeleteNote: (Long) -> Unit,
    onToggleFavorite: (Long, Boolean) -> Unit
) {
    val networkMonitor: NetworkMonitor = koinInject()
    val isConnected by networkMonitor.isConnected.collectAsState(initial = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
    ) {
        // Network Status Indicator
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = if (isConnected) Color(0xFF4CAF50) else Color(0xFFF44336)
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isConnected) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isConnected) "Online" else "Offline",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        SearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            modifier = Modifier.padding(16.dp)
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (notes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Text("Tidak Ada Catatan.", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 80.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(notes) { note ->
                    NoteItemEntity(
                        note = note,
                        onNoteClick = { onNoteClick(note.id) },
                        onDeleteClick = { onDeleteNote(note.id) },
                        onFavClick = { onToggleFavorite(note.id, note.isFavorite == 1L) }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoritesScreen(
    favoriteNotes: List<NoteEntity>,
    onNoteClick: (Long) -> Unit,
    onToggleFavorite: (Long, Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
    ) {
        Text(
            "Favorite Notes",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(16.dp),
            color = Color(0xFFE91E63)
        )
        
        if (favoriteNotes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Text("Tidak Ada Favorite.", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favoriteNotes) { note ->
                    NoteItemEntity(
                        note = note,
                        onNoteClick = { onNoteClick(note.id) },
                        onDeleteClick = null,
                        onFavClick = { onToggleFavorite(note.id, note.isFavorite == 1L) }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Cari catatan...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        shape = RoundedCornerShape(24.dp),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun NoteItemEntity(
    note: NoteEntity,
    onNoteClick: () -> Unit,
    onDeleteClick: (() -> Unit)?,
    onFavClick: () -> Unit
) {
    val isFav = note.isFavorite == 1L
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().clickable { onNoteClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(note.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(note.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 1)
            }
            Row {
                IconButton(onClick = onFavClick) {
                    Icon(
                        imageVector = if (isFav) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFav) Color.Red else Color.Gray
                    )
                }
                if (onDeleteClick != null) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    sortOrder: String,
    onSortOrderChange: (String) -> Unit
) {
    val deviceInfo: DeviceInfo = koinInject()
    val batteryInfo: BatteryInfo = koinInject()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Dark Mode")
            Switch(checked = isDarkMode, onCheckedChange = onDarkModeChange)
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        Text("Sort Order", style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = sortOrder == "newest", onClick = { onSortOrderChange("newest") })
            Text("Newest First", modifier = Modifier.clickable { onSortOrderChange("newest") })
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = sortOrder == "oldest", onClick = { onSortOrderChange("oldest") })
            Text("Oldest First", modifier = Modifier.clickable { onSortOrderChange("oldest") })
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // Device Info Section
        Text("Device Info", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        DeviceInfoItem("Model", deviceInfo.getDeviceModel())
        DeviceInfoItem("Manufacturer", deviceInfo.getManufacturer())
        DeviceInfoItem("OS Version", deviceInfo.getOsVersion())
        
        // Battery Info (Bonus)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Battery Info (Bonus)", style = MaterialTheme.typography.titleMedium, color = Color(0xFF4CAF50))
        DeviceInfoItem("Level", "${batteryInfo.getBatteryLevel()}%")
        DeviceInfoItem("Status", if (batteryInfo.isCharging()) "Charging" else "Discharging")
    }
}

@Composable
fun DeviceInfoItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
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
fun NoteDetailScreen(note: NoteEntity, onEditClick: (Long) -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        TextButton(onClick = onBack) { Text("← Kembali", color = MaterialTheme.colorScheme.primary) }
        Spacer(modifier = Modifier.height(20.dp))
        ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(note.title.uppercase(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reminder: ${note.reminder}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(note.content, style = MaterialTheme.typography.bodyLarge, lineHeight = 24.sp)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { onEditClick(note.id) }, modifier = Modifier.fillMaxWidth()) { Text("Edit Catatan") }
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

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val timePickerDialog = TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    val selectedCalendar = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth, hourOfDay, minute)
                    }
                    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                    reminder = formatter.format(selectedCalendar.time)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePickerDialog.show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Tambah Catatan", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth())
        
        Box(modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() }) {
            OutlinedTextField(
                value = reminder,
                onValueChange = { },
                label = { Text("Reminder (Klik untuk pilih waktu)") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        
        OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Isi") }, modifier = Modifier.fillMaxWidth().weight(1f))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onBack, modifier = Modifier.weight(1f)) { Text("Batal") }
            Button(onClick = { onSave(title, description, content, reminder) }, modifier = Modifier.weight(1f)) { Text("Simpan") }
        }
    }
}

@Composable
fun EditNoteScreen(note: NoteEntity, onSave: (Long, String, String, String, String) -> Unit, onBack: () -> Unit) {
    var title by remember { mutableStateOf(note.title) }
    var description by remember { mutableStateOf(note.description) }
    var content by remember { mutableStateOf(note.content) }
    var reminder by remember { mutableStateOf(note.reminder) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val timePickerDialog = TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    val selectedCalendar = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth, hourOfDay, minute)
                    }
                    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                    reminder = formatter.format(selectedCalendar.time)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePickerDialog.show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Edit Catatan", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth())
        
        Box(modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() }) {
            OutlinedTextField(
                value = reminder,
                onValueChange = { },
                label = { Text("Reminder (Klik untuk pilih waktu)") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Isi") }, modifier = Modifier.fillMaxWidth().weight(1f))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onBack, modifier = Modifier.weight(1f)) { Text("Batal") }
            Button(onClick = { onSave(note.id, title, description, content, reminder) }, modifier = Modifier.weight(1f)) { Text("Simpan") }
        }
    }
}
