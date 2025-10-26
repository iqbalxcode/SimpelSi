#  SimpelSi (Sistem Informasi Pelaporan Lingkungan)

SimpelSi adalah aplikasi mobile Android untuk **Dinas Lingkungan Hidup Kabupaten Nganjuk**. Aplikasi ini dibangun untuk memudahkan masyarakat dalam melaporkan insiden lingkungan (seperti sampah liar) dan sebagai platform edukasi serta informasi layanan.

## 📱 Tangkapan Layar (Contoh)

| Halaman Login | Halaman Home (Beranda) |
| :---: | :---: |
| ![Halaman Login](httpsLogin.png) | ![Halaman Home](Home%20Page.png) |
| **(Ganti nama file gambar ini agar sesuai dengan file screenshot Anda)** | **(Ganti nama file gambar ini agar sesuai dengan file screenshot Anda)** |

**Catatan:** Agar gambar tampil, Anda harus menambahkan file screenshot (misal: `LoginPage.png` & `HomePage.png`) ke dalam folder proyek Anda dan pastikan namanya sama.

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
* **Pengiriman Email (OTP):** [PHPMailer](https://github.com/PHPMailer/PHPMailer) (direncanakan, via SMTP Gmail)

## 🚀 Cara Menjalankan Proyek

Untuk menjalankan proyek ini, Anda perlu mengatur *backend* dan *frontend*.

### 1. Backend (Server PHP)

1.  **Database:**
    * Buat database di hosting Anda (misal: `b31_40251637_simpelsi` di ByetHost).
    * Impor file `.sql` (jika ada) atau buat tabel secara manual (`admin`, `masyarakat`) sesuai struktur.
    * Isi data dummy jika diperlukan.
2.  **Upload File API:**
    * Upload semua file PHP (`login.php`, `get_masyarakat.php`, dll.) ke folder `htdocs` di hosting Anda.
3.  **Konfigurasi PHP:**
    * Buka setiap file PHP dan ubah variabel koneksi database:
        ```php
        $servername = "sql305.byetcluster.com";
        $dbname = "b31_40251637_simpelsi";
        $username = "b31_40251637";
        $password = "password_anda"; // ⚠️ GANTI INI
        ```

### 2. Frontend (Android Studio)

1.  **Clone Repository:**
    ```bash
    git clone [https://url-repository-anda.git](https://url-repository-anda.git)
    ```
2.  **Buka Proyek:** Buka folder proyek di Android Studio.
3.  **Konfigurasi `ApiClient.java`:**
    * Buka file `api/ApiClient.java`.
    * Pastikan `BASE_URL` sudah sesuai dengan alamat web Anda:
        ```java
        public static final String BASE_URL = "[https://simpelsi.byethost31.com/](https://simpelsi.byethost31.com/)";
        ```
4.  **⚠️ PENTING: Penanganan Anti-Bot ByetHost**
    * Hosting gratis ByetHost memiliki sistem anti-bot yang memblokir permintaan dari luar browser (termasuk Android/Retrofit).
    * Solusinya adalah dengan "menyamar" sebagai browser dengan menambahkan `Cookie` dan `User-Agent`.
    * Buka `api/ApiClient.java` dan cari bagian `Interceptor`.
    * **Setiap kali aplikasi error (gagal koneksi / malformed JSON):**
        1.  Buka `BASE_URL` Anda di browser (Chrome/Firefox).
        2.  Tekan **F12** (Developer Tools) -> tab **Application** (Aplikasi) -> **Cookies**.
        3.  Salin nilai cookie (misal: `__test=...`).
        4.  Kembali ke `ApiClient.java` dan tempelkan nilai `cookie` dan `userAgent` yang baru.
        ```java
        // Di dalam ApiClient.java
        String cookie = "NILAI_COOKIE_BARU_ANDA";
        String userAgent = "USER_AGENT_BROWSER_ANDA";
        ```
        5.  Build ulang aplikasi.

## 📄 API Endpoints (Contoh)

* `POST /login.php`
    * Body: `{"email": "...", "password": "..."}`
    * Respons: `{"status": "success", "data": {...}}` atau `{"status": "error", "message": "..."}`
* `GET /get_masyarakat.php`
    * Respons: `{"status": "success", "data": [...]}`
