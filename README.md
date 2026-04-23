# Tugas Pengembangan Aplikasi Mobile

Repository ini berisi kumpulan tugas mata kuliah Pemrograman Mobile yang mencakup pengembangan aplikasi Android menggunakan teknologi modern seperti **Jetpack Compose**, **Kotlin Coroutines**, dan **Compose Multiplatform**.

## 📂 Daftar Proyek

### 1. [NewsFeedSimulator](./NewsFeedSimulator) (Tugas 2)
**Topik: Advanced Kotlin, Coroutines, dan Flow**
Aplikasi simulasi berita yang mendemonstrasikan pengolahan data asinkron secara real-time.
- **Fitur**: Stream berita otomatis, Filter kategori dinamis (Tech, Sports, Finance), dan Counter berita yang dibaca.
- **Teknologi**: Kotlin Flow (StateFlow), Coroutines (viewModelScope), MVVM Architecture, Jetpack Compose.

### 2. [MyProfileApp](./MyProfileApp) (Tugas 3)
**Topik: Compose Multiplatform Basics**
Aplikasi profil pribadi sederhana yang dibangun menggunakan framework Compose Multiplatform.
- **Fitur**: Menampilkan informasi profil, foto, dan integrasi UI dasar.
- **Teknologi**: Compose Multiplatform, Kotlin, Material Design 3.

### 3. [MyProfileAppV2](./MyProfileAppV2) (Tugas 4)
**Topik: State Management dan MVVM**
Pengembangan aplikasi profil dengan manajemen state yang lebih terstruktur.
- **Fitur**: Manajemen data profil menggunakan ViewModel, sinkronisasi state antar komponen UI, dan implementasi arsitektur MVVM yang bersih.
- **Teknologi**: Jetpack Compose, ViewModel, StateFlow/LiveData, Material Design 3.

### 4. [NotesAppNavigation](./NotesAppNavigation) (Tugas Praktikum Minggu 5)
**Topik: Jetpack Compose Navigation & State Management**
Aplikasi catatan (Notes App) yang mendemonstrasikan sistem navigasi yang kompleks dan pengelolaan data dinamis (CRUD sederhana).
- **Fitur**: Bottom Navigation, Dynamic Arguments, Create/Read/Update/Delete catatan, dan fitur Favorites.
- **Teknologi**: Jetpack Compose Navigation, State Management (mutableStateListOf), Material Design 3.

### 5. [NewsReaderApp](./NewsReaderApp) (Tugas Praktikum Minggu 6)
**Topik: Networking, Local Persistence (Room), dan Repository Pattern**
Aplikasi pembaca berita Indonesia yang mendukung fitur offline caching dan performansi UI yang halus.
- **Fitur**: Offline Caching (Room DB), Shimmer Loading Effect, Chrome Custom Tabs, dan Dark Mode.
- **Teknologi**: Jetpack Compose, Ktor Client (Networking), Room Database, Repository Pattern, Coil (Image Loading).

### 6. [NotesAppNavigationV2](./NotesAppNavigationV2) (Tugas Praktikum Minggu 7)
**Topik: Local Database (SQLDelight), DataStore, & Advanced UI States**
Versi lanjutan dari Notes App dengan persistensi data yang kuat dan fitur pencarian/sortir yang dioptimasi.
- **Fitur**: Offline-First dengan SQLDelight, Theme Persistence (DataStore), Real-time Search, Sortir catatan, dan Date/Time Picker.
- **Teknologi**: SQLDelight (SQLite), Jetpack DataStore, Ktor Client (Sync), Material 3, MVVM Architecture.

### 7. [NotesAppNavigationV3](./NotesAppNavigationV3) (Tugas Praktikum Minggu 8)
**Topik: Dependency Injection (Koin) & Platform Specific (Expect/Actual)**
Upgrade besar pada Notes App dengan implementasi arsitektur yang modular dan fitur hardware-aware.
- **Fitur**: Koin DI Setup, Network Monitoring (Online/Offline indicator), Device Info & Battery Status (Expect/Actual pattern).
- **Teknologi**: Koin DI, Expect/Actual Pattern, SQLDelight, DataStore, Jetpack Compose.

---

## ⚙️ Cara Menjalankan
1. Pastikan Anda memiliki **Android Studio** versi terbaru.
2. Clone repository ini.
3. Untuk menjalankan salah satu tugas, buka folder tugas yang spesifik (misal: `NotesAppNavigationV3`) sebagai proyek utama di Android Studio.
4. Tunggu proses **Gradle Sync** hingga selesai.
5. Jalankan aplikasi menggunakan Emulator atau Perangkat Android fisik.

---
**Dibuat oleh:**
- **Nama**: Nahli Saud Ramdani
- **NIM**: 123140049
