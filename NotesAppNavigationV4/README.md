# NotesAppNavigationV4

#### Nama : Nahli Saud Ramdani  
#### NIM  : 123140049  
#### Kelas: PAM RA  

## Deskripsi
**NotesAppNavigationV4** adalah aplikasi catatan (notes) berbasis **Compose Multiplatform (Kotlin Multiplatform)** yang berjalan di **Android** dan **iOS** melalui module `composeApp`.

Project ini menggunakan arsitektur Compose dan memanfaatkan **navigation** untuk perpindahan antar halaman (versi V4).

## Tech Stack
- Kotlin Multiplatform / Compose Multiplatform
- Compose Material 3
- Android target + iOS target (iosArm64, iosSimulatorArm64)
- Ktor Client + Kotlinx Serialization (dependensi tersedia pada project)

## Struktur Project (ringkas)
- `NotesAppNavigationV4/composeApp` : source utama aplikasi Compose Multiplatform
- `NotesAppNavigationV4/androidMain` : konfigurasi/platform-specific Android (di dalam `composeApp/src/androidMain`)
- `NotesAppNavigationV4/iosApp` : entry point iOS
- `NotesAppNavigationV4/settings.gradle.kts` : konfigurasi root project (include `:composeApp`)

## Konfigurasi Penting
- **Root project name**: `Pocketwise` (sesuai `settings.gradle.kts`)
- **Android namespace & applicationId**: `com.rapi.NoteAppsNavigationV4` (sesuai `composeApp/build.gradle.kts`)

## Cara Menjalankan (Android)
1. Buka folder `NotesAppNavigationV4` menggunakan Android Studio.
2. Tunggu proses Gradle Sync sampai selesai.
3. Jalankan konfigurasi **composeApp** di emulator/device Android.

## Cara Menjalankan (iOS)
1. Buka folder `NotesAppNavigationV4` di lingkungan Kotlin Multiplatform yang sesuai.
2. Jalankan target iOS melalui konfigurasi Compose Multiplatform (misalnya via Xcode project pada folder `iosApp` jika sudah terkonfigurasi).

## Screenshot
