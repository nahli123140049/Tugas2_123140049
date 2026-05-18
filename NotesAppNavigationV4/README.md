#NotesAppNavigation - Tugas Praktikum Minggu 10 📝🧪

Aplikasi **NoteAppNavigaton** adalah aplikasi manajemen catatan berbasis Android yang mengimplementasikan standar arsitektur modern dengan **Dependency Injection**, **Reactive Programming**, dan **Automated Testing**.

---

## 🏗️ Architecture & Tech Stack

Project ini menggunakan pemisahan tanggung jawab yang jelas untuk meningkatkan skalabilitas dan kemudahan pengujian:

```mermaid
graph TD
    subgraph "UI Layer (Jetpack Compose)"
        MainScreen --> NoteListScreen
        MainScreen --> FavoritesScreen
        MainScreen --> NoteDetailScreen
        NoteScreenTest[NoteScreenTest.kt] -.-> |UI Tests| NoteListScreen
    end

    subgraph "Dependency Injection (Koin)"
        dataModule[AppModule.kt: dataModule]
        viewModelModule[AppModule.kt: viewModelModule]
    end

    subgraph "Logic & Data Layer"
        NoteViewModel --> NoteRepository
        NoteRepository --> SQLDelight[(SQLite Database)]
        NoteRepository --> SettingsDataStore[(Preferences)]
        
        NoteViewModelTest[ViewModelTest - MockK] -.-> NoteViewModel
        NoteRepositoryTest[RepoTest - In-Memory] -.-> NoteRepository
    end

    %% DI Connections
    dataModule -.-> |Provides| NoteRepository
    viewModelModule -.-> |Inject| NoteViewModel
```

---

## 📝 Deskripsi Tugas & Fitur

1.  **Koin Dependency Injection**: Implementasi `AppModule.kt` untuk mengelola instansi Singleton (Repository, Database) dan ViewModel secara otomatis, mengurangi *boilerplate code*.
2.  **SQLDelight Persistence**: Menggunakan SQLDelight untuk manajemen database yang *type-safe* dan reaktif (menggunakan Flow untuk memantau perubahan data).
3.  **Unit Testing (Logic)**: 
    - **ViewModel Test**: Menguji logika pencarian, penghapusan, dan pembaruan status favorit menggunakan **MockK**.
    - **Repository Test**: Memastikan query SQL bekerja dengan benar menggunakan **In-Memory SQLite Driver**.
4.  **UI Testing (Compose)**: Pengujian otomatis pada `NoteScreenTest.kt` menggunakan `createComposeRule()` untuk memverifikasi komponen UI seperti list catatan dan *empty state*.
5.  **Platform Integration**: Penambahan fitur `DeviceInfo`, `BatteryInfo`, dan `NetworkMonitor` yang di-inject via Koin untuk memantau status perangkat secara *real-time*.
6.  **Code Coverage**: Verifikasi kualitas pengujian melalui fitur *Run with Coverage* di Android Studio.

---

## 📸 Dokumentasi Pengujian

| Unit Test & Repository Test | Code Coverage Report |
| :---: | :---: |
| ![Unit Test](screenshot/Unit_test_result.png) | ![Coverage](screenshot/Run%20with%20Coverage.png) |

---

## 🎥 Video Demo
https://github.com/user-attachments/assets/af8590a8-ea2b-4b2d-9f4c-e84dc05a5e24

---
**Identitas Mahasiswa:**
- **Nama**: Nahli Saud Ramdani
- **NIM**: 123140049
- **Kelas**: Pengembangan Aplikasi Mobile (PAM)

---
*Dibuat untuk memenuhi tugas Praktikum Minggu 10 - Testing & DI*
