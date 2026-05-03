# Tugas Praktikum 9

#### Nama : Nahli Saud Ramdani
#### NIM  : 123140049
#### Kelas: PAM RA

## Deskripsi
Pengembangan aplikasi **PocketWise**, yaitu aplikasi pencatat pengeluaran pribadi berbasis **Kotlin Multiplatform** yang terintegrasi dengan **Gemini API**.

Aplikasi ini digunakan untuk mencatat pengeluaran berdasarkan nominal, kategori, dan catatan. Data pengeluaran kemudian dihitung secara lokal oleh aplikasi untuk mendapatkan total pengeluaran, jumlah transaksi, dan kategori pengeluaran terbesar.

Fitur AI digunakan untuk memberikan analisis pengeluaran dan saran penghematan berdasarkan data yang dimasukkan pengguna.

## Fitur Utama
- Menambahkan data pengeluaran berdasarkan nominal, kategori, dan catatan
- Menampilkan daftar pengeluaran
- Menghitung total pengeluaran secara lokal
- Menampilkan jumlah transaksi dan kategori pengeluaran terbesar
- Mengintegrasikan **Gemini API** untuk analisis pengeluaran
- Menggunakan prompt yang dirancang untuk asisten keuangan mahasiswa
- Menampilkan loading state saat AI memproses data
- Menangani error seperti API key tidak valid, timeout, model tidak ditemukan, dan quota/rate limit

##  AI Integration
PocketWise menggunakan **Gemini API** untuk menganalisis data pengeluaran pengguna.

Alur AI pada aplikasi:
1. Pengguna memasukkan data pengeluaran
2. Aplikasi menghitung ringkasan pengeluaran secara lokal
3. Ringkasan dan detail transaksi dikirim ke Gemini API
4. Gemini memberikan analisis dan saran penghematan
5. Hasil analisis ditampilkan pada aplikasi

Perhitungan angka seperti total pengeluaran dan kategori terbesar dilakukan oleh aplikasi, bukan oleh AI, agar hasil lebih akurat.

## Prompt Engineering
Prompt dirancang dengan beberapa bagian utama:
- **Role**: Gemini berperan sebagai asisten keuangan pribadi untuk mahasiswa
- **Task**: Menganalisis data pengeluaran dan memberi saran hemat
- **Rules**:
  - Gunakan Bahasa Indonesia
  - Jangan mengarang angka di luar data yang diberikan
  - Jangan memberikan rekomendasi produk keuangan tertentu
  - Fokus pada pengelolaan pengeluaran harian
- **Format Output**:
  1. Ringkasan Kondisi Keuangan
  2. Pengeluaran Terbesar
  3. Pola yang Terlihat
  4. Risiko Jika Dibiarkan
  5. Saran Penghematan
  6. Target Minggu Depan
 
## Error Handling dan Fallback Mode
Aplikasi menangani beberapa kondisi error, seperti:
- API key kosong atau tidak valid
- Prompt kosong
- Request Gemini tidak valid
- Model Gemini tidak ditemukan
- Gemini API terkena quota atau rate limit
- Request timeout
- Gemini tidak mengembalikan respons
- Belum ada data pengeluaran untuk dianalisis
- Nominal pengeluaran tidak valid

Jika Gemini API tidak tersedia karena quota atau rate limit, aplikasi tetap memberikan feedback melalui fallback analysis lokal berdasarkan data pengeluaran yang sudah dihitung aplikasi.

## Cara Menjalankan (Android Studio)
1. Pilih branch **week9**
2. Clone / download repository:
   - `https://github.com/raapstronaut/Praktikum-PAM.git`
3. Buka folder project tugas praktikum 9 menggunakan Android Studio.
4. Tunggu proses **Gradle Sync** sampai selesai.
5. Jalankan aplikasi dengan menekan tombol **Run**.
6. Pilih emulator/device Android, lalu aplikasi akan terbuka.

## Screenshot Aplikasi

<table>
  <tr>
    <td align="center"><b>1. Input Expense</b></td>
    <td align="center"><b>2. Expense Summary</b></td>
    <td align="center"><b>3. Loading State</b></td>
    <td align="center"><b>4. AI Analysis</b></td>
    <td align="center"><b>5. Error Handling</b></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/44709b46-efdb-462d-a7ae-2d82b1b23a9f" width="220" /></td>
    <td><img src="https://github.com/user-attachments/assets/f9a9fba0-5cd7-4d7a-83e9-3fc1d1efd5f3" width="220"/></td>
    <td><img src="https://github.com/user-attachments/assets/fa3cf673-18e0-4674-bb5b-10101b45b26c" width="220"/></td>
    <td><img src="https://github.com/user-attachments/assets/e89d5f68-f2d2-43ac-b399-779b5eccb6a6" width="220"/></td>
    <td><img src="https://github.com/user-attachments/assets/18608a3d-6d91-4fcf-b6b0-3a08132772bf" width="220"/></td>
  </tr>
</table>
