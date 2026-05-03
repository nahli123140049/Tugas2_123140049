# TUGAS PRAKTIKUM MINGGU 9 — Notes App + AI Integration 🤖

Aplikasi Notes ini telah diintegrasikan dengan **Gemini AI** (Google Generative Language API — model `gemini-1.5-flash`) untuk menghadirkan empat fitur kecerdasan buatan langsung di dalam alur pembuatan dan pengeditan catatan.

> **Base project:** NotesAppNavigationV3 (Week 8) — semua fitur sebelumnya (SQLDelight, DataStore, Koin DI, Platform Features) tetap tersedia.

---

## 🤖 Fitur AI

| Fitur | Deskripsi | Lokasi |
|-------|-----------|--------|
| **Summarize** | Meringkas isi catatan menjadi 2–3 kalimat padat | Add/Edit Note screen |
| **Rewrite** | Menulis ulang teks agar lebih jelas dan terstruktur | Add/Edit Note screen |
| **Translate** | Menerjemahkan teks Indonesia → Inggris | Add/Edit Note screen |
| **AI Assistant** | Chat multi-turn berbasis Gemini untuk brainstorming, menyusun catatan, dan tanya jawab | Tab "AI" di bottom bar |

### Bonus
- ✅ **Multi-turn conversation** — riwayat percakapan dipertahankan selama sesi
- ✅ **Streaming response** — teks AI muncul karakter-per-karakter (SSE via `streamGenerateContent`)

---

## 🏗️ Architecture

```
NotesAppNavigationV4/
└── app/src/main/java/com/example/notesappnavigation/
    ├── ai/
    │   ├── GeminiModels.kt      # @Serializable request/response data classes
    │   ├── GeminiClient.kt      # Ktor HTTP client (generate + stream) + retry logic
    │   ├── AiRepository.kt      # Business logic + system prompts
    │   ├── AiState.kt           # Sealed class: Idle / Loading / Success / Error
    │   ├── AiViewModel.kt       # ViewModel untuk summarize/rewrite/translate
    │   └── ChatViewModel.kt     # ViewModel untuk multi-turn chat (streaming)
    ├── screens/
    │   ├── NoteScreens.kt       # + AI action buttons di Add/Edit screens
    │   └── AiAssistantScreen.kt # Chat UI dengan streaming
    ├── navigation/
    │   ├── Screen.kt            # + AiAssistant route
    │   └── BottomNavItem.kt     # + 5th tab "AI"
    └── di/
        └── AppModule.kt         # + GeminiClient, AiRepository, AiViewModel, ChatViewModel
```

### Layer Diagram

```
UI (Compose Screens)
    └── ViewModel (AiViewModel / ChatViewModel)
            └── AiRepository  [system prompts, use-case logic]
                    └── GeminiClient  [HTTP + retry + streaming]
                                └── Gemini REST API
```

---

## 🔒 Setup API Key (Aman — Tidak Di-commit)

1. Dapatkan API key gratis di [Google AI Studio](https://aistudio.google.com/app/apikey).

2. Buat file `NotesAppNavigationV4/local.properties` (**sudah ada di `.gitignore`**, tidak akan ter-commit):

   ```properties
   sdk.dir=/path/to/your/android/sdk
   GEMINI_API_KEY=AIzaSy...your_key_here
   ```

   > Lihat `secrets.properties.example` untuk format referensi.

3. Build & run — key otomatis dibaca via `BuildConfig.GEMINI_API_KEY` saat Gradle sync.

⚠️ **Jangan pernah hardcode API key di source code atau commit ke repository.**

---

## 🚀 Cara Menjalankan

```bash
# Clone repo
git clone https://github.com/nahli123140049/Tugas2_123140049.git
cd Tugas2_123140049/NotesAppNavigationV4

# Tambahkan API key (lihat bagian Setup di atas)
echo "GEMINI_API_KEY=your_key_here" >> local.properties

# Buka di Android Studio, sync Gradle, lalu Run
```

Minimum SDK: **24 (Android 7.0)**

---

## 💡 Cara Pakai Fitur AI

### Summarize / Rewrite / Translate (di Add/Edit Note)
1. Buka catatan atau tambah catatan baru.
2. Isi field **"Isi"**.
3. Tekan tombol **Summarize**, **Rewrite**, atau **Translate** di bagian bawah.
4. Tunggu sebentar (loading indicator muncul) — hasil AI otomatis menggantikan field "Isi".
5. Jika terjadi error, Snackbar akan muncul dengan pesan error; coba lagi dengan menekan tombol yang sama.

### AI Assistant (tab "AI" di bottom bar)
1. Tap ikon ⭐ **AI** di navigation bar bawah.
2. Ketik pesan di kolom input, tekan kirim (ikon panah).
3. Respons AI muncul secara **streaming** (teks muncul bertahap).
4. Percakapan bersifat **multi-turn** — konteks diingat selama sesi.

---

## ⚙️ Error Handling & Retry

| Kondisi | Penanganan |
|---------|------------|
| HTTP 429 (rate limit) | Retry otomatis hingga 3× dengan exponential backoff (1s → 2s → 4s) |
| HTTP 5xx (server error) | Retry otomatis (sama seperti di atas) |
| Timeout / network error | Retry otomatis |
| Error lainnya | Snackbar UI dengan pesan ramah |

---

## ✅ Rubrik Penilaian

| Komponen | Status |
|----------|--------|
| **Integrasi AI (Gemini)** | ✅ Summarize, Rewrite, Translate, Chat |
| **Prompt Engineering** | ✅ System prompt per fitur, output terstruktur |
| **Error Handling + Retry** | ✅ Exponential backoff, Snackbar UI |
| **UI Responsif + Loading** | ✅ CircularProgress, LinearProgress (streaming) |
| **Code Quality** | ✅ Service layer terpisah (GeminiClient / AiRepository / ViewModel) |
| **Dokumentasi README** | ✅ Dokumen ini |
| **Bonus: Multi-turn** | ✅ +10% |
| **Bonus: Streaming** | ✅ +5% |

---

## 📸 Struktur Navigasi

```
Bottom Nav:
  [Notes] [Favorites] [Profile] [Settings] [AI ⭐]

Notes → NoteDetail → EditNote (+ AI buttons)
     → AddNote (+ AI buttons)
AI tab → AiAssistantScreen (chat)
```

---

*Dibuat oleh: **Nahli Saud Ramdani** — NIM 123140049*
