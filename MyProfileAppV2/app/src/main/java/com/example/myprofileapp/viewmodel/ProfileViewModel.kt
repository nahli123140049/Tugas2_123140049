package com.example.myprofileapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myprofileapp.data.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun updateEditName(newVal: String) { _uiState.update { it.copy(editName = newVal) } }
    fun updateEditBio(newVal: String) { _uiState.update { it.copy(editBio = newVal) } }
    fun updateEditEmail(newVal: String) { _uiState.update { it.copy(editEmail = newVal) } }
    fun updateEditPhone(newVal: String) { _uiState.update { it.copy(editPhone = newVal) } }
    fun updateEditLocation(newVal: String) { _uiState.update { it.copy(editLocation = newVal) } }

    fun toggleDarkMode(darkMode: Boolean) {
        _uiState.update { it.copy(isDarkMode = darkMode) }
    }

    fun toggleEditMode() {
        _uiState.update {
            val enteringEdit = !it.isEditMode
            it.copy(
                isEditMode = enteringEdit,
                // Sinkronisasi data asli ke form edit
                editName = if (enteringEdit) it.name else "",
                editBio = if (enteringEdit) it.bio else "",
                editEmail = if (enteringEdit) it.email else "",
                editPhone = if (enteringEdit) it.phone else "",
                editLocation = if (enteringEdit) it.location else ""
            )
        }
    }

    fun saveProfile() {
        _uiState.update {
            it.copy(
                name = it.editName,
                bio = it.editBio,
                email = it.editEmail,
                phone = it.editPhone,
                location = it.editLocation,
                isEditMode = false
            )
        }
    }
}