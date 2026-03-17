package com.example.myprofileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myprofileapp.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MyProfileApp()
            }
        }
    }
}

@Composable
fun MyProfileApp(viewModel: ProfileViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // FITUR 4: Siapkan controller untuk Haptic Feedback (Getaran)
    val haptic = LocalHapticFeedback.current

    // FITUR 4: Background Gradient Dinamis biar Glassmorphism-nya keliatan
    val backgroundBrush = animateColorAsState(
        targetValue = if (uiState.isDarkMode) Color(0xFF0F2027) else Color(0xFFE8CBC0),
        animationSpec = tween(700)
    )
    val backgroundBrush2 = animateColorAsState(
        targetValue = if (uiState.isDarkMode) Color(0xFF203A43) else Color(0xFF636FA4),
        animationSpec = tween(700)
    )


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(backgroundBrush.value, backgroundBrush2.value)
                    )
                )
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()) // Biar bisa discroll kalau kepanjangan
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Switch Dark Mode + Haptic
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dark Mode", color = Color.White, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = uiState.isDarkMode,
                        onCheckedChange = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.toggleDarkMode(it)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF6DD5ED))
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Profile Header dengan Completeness Meter
                ProfileHeader(
                    name = uiState.name,
                    isDarkMode = uiState.isDarkMode,
                    completeness = uiState.completeness
                )

                Spacer(modifier = Modifier.height(24.dp))

                AnimatedContent(
                    targetState = uiState.isEditMode,
                    label = "FormTransition"
                ) { isEditing ->
                    if (isEditing) {
                        // Form Edit Lengkap
                        EditProfileForm(
                            uiState = uiState,
                            viewModel = viewModel,
                            onCancel = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.toggleEditMode()
                            },
                            onSave = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.saveProfile()
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Profile berhasil diupdate bray!")
                                }
                            }
                        )
                    } else {
                        // Tampilan Profile + Glassmorphism Card
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = uiState.bio,
                                textAlign = TextAlign.Center,
                                color = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // FITUR 4: Glassmorphism Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White.copy(alpha = 0.15f) // Efek kaca transparan
                                ),
                                elevation = CardDefaults.cardElevation(0.dp),
                                shape = RoundedCornerShape(24.dp),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    InfoItem(Icons.Default.Email, "Email", uiState.email.ifBlank { "Belum diisi" })
                                    InfoItem(Icons.Default.Phone, "Phone", uiState.phone.ifBlank { "Belum diisi" })
                                    InfoItem(Icons.Default.LocationOn, "Location", uiState.location.ifBlank { "Belum diisi" })
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.toggleEditMode()
                                },
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6DD5ED))
                            ) {
                                Text("Edit Profile", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditProfileForm(
    uiState: com.example.myprofileapp.data.ProfileUiState,
    viewModel: ProfileViewModel,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    val fieldColor = Color.White
    val errorColor = Color(0xFFFF6B6B)

    // Glassmorphism Container buat form
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CustomTextField("Nama Lengkap", uiState.editName, { viewModel.updateEditName(it) }, fieldColor, errorColor, isRequired = true)
        CustomTextField("Bio", uiState.editBio, { viewModel.updateEditBio(it) }, fieldColor, errorColor, isRequired = true)
        CustomTextField("Email (Opsional)", uiState.editEmail, { viewModel.updateEditEmail(it) }, fieldColor, errorColor, isRequired = false)
        CustomTextField("No. Telepon (Opsional)", uiState.editPhone, { viewModel.updateEditPhone(it) }, fieldColor, errorColor, isRequired = false)
        CustomTextField("Lokasi (Opsional)", uiState.editLocation, { viewModel.updateEditLocation(it) }, fieldColor, errorColor, isRequired = false)

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Text("Cancel")
            }
            Button(
                onClick = onSave,
                modifier = Modifier.weight(1f),
                enabled = uiState.isSaveEnabled,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6DD5ED), disabledContainerColor = Color.Gray)
            ) {
                Text("Save", color = if (uiState.isSaveEnabled) Color.Black else Color.DarkGray)
            }
        }
    }
}

// Komponen bantuan biar kodenya gak kepanjangan
@Composable
fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    color: Color,
    errorColor: Color,
    isRequired: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = color.copy(alpha = 0.7f)) },
        isError = isRequired && value.isBlank(),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = color, unfocusedTextColor = color,
            focusedBorderColor = color, unfocusedBorderColor = color.copy(alpha = 0.5f),
            errorTextColor = errorColor, errorBorderColor = errorColor, errorLabelColor = errorColor
        )
    )
}

@Composable
fun InfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.White.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
            Text(text = value, fontWeight = FontWeight.Medium, color = Color.White)
        }
    }
}

@Composable
fun ProfileHeader(name: String, isDarkMode: Boolean, completeness: Float) {
    // FITUR 2: Animasi Progress Meter
    val animatedProgress by animateFloatAsState(
        targetValue = completeness,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            // Lingkaran Progress Completeness
            CircularProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.size(160.dp),
                color = Color(0xFF00FF87), // Hijau neon
                strokeWidth = 6.dp,
                trackColor = Color.White.copy(alpha = 0.2f)
            )

            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(135.dp)
                    .clip(CircleShape)
                    .border(3.dp, Color.Transparent, CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(text = "Profile ${ (completeness * 100).toInt() }% Lengkap", fontSize = 12.sp, color = Color(0xFF00FF87))
    }
}
