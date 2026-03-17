# Tugas 2 - 123140049

Aplikasi Android "News Feed Simulator" yang mendemonstrasikan implementasi **MVVM Architecture**, **Kotlin Coroutines**, dan **Asynchronous Flow** menggunakan **Jetpack Compose**.

## 🚀 Fitur Utama

- **Real-time News Stream**: Mensimulasikan data berita yang masuk setiap 2 detik menggunakan `flow { ... }`.
- **Dynamic Category Filtering**: Pengguna dapat memfilter berita berdasarkan kategori (**Tech**, **Sports**, **Finance**) secara dinamis melalui UI.
- **Reactive State Management**: Menggunakan `StateFlow` di dalam `ViewModel` untuk mengelola daftar berita, kategori terpilih, dan jumlah total berita yang telah dibaca.
- **Automatic Counter**: Setiap berita baru yang muncul sesuai filter akan otomatis menambah hitungan "Total Dibaca".
- **Modern UI Components**:
    - `LazyColumn` untuk daftar berita yang efisien.
    - `FilterChip` untuk pemilihan kategori.
    - `Card` dengan desain material 3 yang responsif.
    - Warna kategori yang berbeda (Tech: Biru, Sports: Hijau, Finance: Orange).

## 🛠️ Arsitektur & Teknologi

- **Pattern**: MVVM (Model-View-ViewModel).
- **Jetpack Compose**: Untuk UI deklaratif.
- **ViewModel**: Mengelola state dan logika bisnis agar bertahan saat perubahan konfigurasi.
- **Kotlin Flow**:
    - `Flow`: Untuk stream berita asinkron.
    - `StateFlow`: Untuk state UI yang reaktif.
    - `filter` & `collect`: Untuk pengolahan data stream.
- **Coroutines (viewModelScope)**: Untuk menjalankan tugas asinkron di latar belakang.

## 📂 Struktur Proyek

- `News.kt`: Model data untuk objek berita.
- `NewsManager.kt`: Sumber data (Repository-like) yang menghasilkan flow berita dan mengelola counter.
- `NewsViewModel.kt`: Penghubung antara data dan UI, menangani logika filter kategori.
- `NewsScreen.kt`: Komponen UI Compose (Screen, Cards, Chips).
- `MainActivity.kt`: Entry point aplikasi.

## ⚙️ Cara Menjalankan Proyek

1.  **Buka di Android Studio**: Pastikan menggunakan versi terbaru (Ladybug atau lebih baru).
2.  **Sync Gradle**: Biarkan Android Studio mengunduh dependensi (Compose, Coroutines, Lifecycle).
3.  **Run**: Jalankan di Emulator atau Perangkat Fisik (Min SDK 24).
4.  **Interaksi**:
    - Pilih salah satu kategori (Tech/Sports/Finance) pada tombol chip di bagian atas.
    - Perhatikan berita baru muncul setiap 2 detik.
    - Lihat "Total Dibaca" bertambah secara otomatis setiap ada berita baru yang masuk.

---
**Dibuat oleh:**
Nama: Nahli Saud Ramdani
NIM: 123140049
