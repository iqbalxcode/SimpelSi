#  SimpelSi (Sistem Pelaporan Sampah Ilegal)

SimpelSi adalah aplikasi mobile Android untuk **Dinas Lingkungan Hidup Kabupaten Nganjuk**. Aplikasi ini dibangun untuk memudahkan masyarakat dalam melaporkan insiden lingkungan (seperti sampah liar) dan sebagai platform edukasi serta informasi layanan.



## ✨ Fitur Utama

* **Autentikasi Pengguna:**
    * Login dengan Email & Password (terhubung ke tabel `masyarakat`).
    * Registrasi Akun Baru.
    * Lupa Password (direncanakan dengan OTP via email).
    * Login dengan Google (direncanakan).
* **Beranda (Dashboard):**
    * Menampilkan Visi & Misi Dinas.
    * Pintasan menu layanan utama.
    * Menampilkan dokumentasi kegiatan terbaru.
* **Manajemen Laporan:**
    * Formulir Pengajuan Laporan (mengirim data ke server).
    * Melihat Status Laporan (menunggu, diproses, selesai).
* **Fitur Informasi:**
    * Artikel Edukasi Lingkungan.
    * Info lokasi TPS (Tempat Pembuangan Sementara).
* **Manajemen Akun:**
    * Melihat dan mengedit profil pengguna.

## 🛠️ Teknologi yang Digunakan

Proyek ini dibagi menjadi dua bagian: aplikasi *frontend* (Android) dan *backend* (API).

### 1. Frontend (Aplikasi Android)

* **Bahasa:** Java
* **Arsitektur:** Standard Android Activity Lifecycle
* **Networking:** [Retrofit 2](https://square.github.io/retrofit/) (untuk memanggil API)
* **JSON Parsing:** [Gson](https://github.com/google/gson) (untuk mengubah data JSON ke objek Java)
* **Desain:** Material Design Components

### 2. Backend (API & Database)

* **Bahasa:** PHP (prosedural)
* **Database:** MySQL
* **Hosting:** ByetHost (Hosting Gratis)
