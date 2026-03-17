package com.example.myprofileapp.data

data class ProfileUiState(
    // Data Profile Asli
    val name: String = "Nahli Saud Ramdani",
    val bio: String = "Mahasiswa Teknik Informatika ITERA yang tertarik pada AI dan Machine Learning.",
    val email: String = "nahli.123140049@student.itera.ac.id",
    val phone: String = "08551744591",
    val location: String = "Lampung, Indonesia",

    // Data Form Edit Sementara
    val editName: String = "",
    val editBio: String = "",
    val editEmail: String = "",
    val editPhone: String = "",
    val editLocation: String = "",

    // Status UI
    val isDarkMode: Boolean = false,
    val isEditMode: Boolean = false
) {
    // Tombol save cuma nyala kalau field WAJIB (Nama & Bio) gak kosong dan ada yang berubah
    val isSaveEnabled: Boolean
        get() = editName.isNotBlank() && editBio.isNotBlank() &&
                (editName != name || editBio != bio || editEmail != email || editPhone != phone || editLocation != location)

    // FITUR 2: Menghitung persentase kelengkapan profil (0.0 sampai 1.0)
    val completeness: Float
        get() {
            var filled = 0f
            if (name.isNotBlank()) filled += 1f
            if (bio.isNotBlank()) filled += 1f
            if (email.isNotBlank()) filled += 1f
            if (phone.isNotBlank()) filled += 1f
            if (location.isNotBlank()) filled += 1f
            return filled / 5f // Ada 5 field total
        }
}