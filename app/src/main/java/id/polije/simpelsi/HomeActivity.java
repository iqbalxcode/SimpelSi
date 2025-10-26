package id.polije.simpelsi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    // Deklarasi variabel untuk elemen yang bisa diklik
    private LinearLayout layananCekStatus, layananArtikel, layananInfoTPS;
    private LinearLayout navLaporan, navProfil, headerDokumentasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Panggil method untuk inisialisasi view
        initViews();

        // Panggil method untuk setup listener
        setupClickListeners();
    }

    /**
     * Method untuk menghubungkan variabel Java dengan ID di XML
     */
    private void initViews() {
        // Tombol Layanan
        layananCekStatus = findViewById(R.id.layananCekStatus);
        layananArtikel = findViewById(R.id.layananArtikel);
        layananInfoTPS = findViewById(R.id.layananInfoTPS);

        // Tombol Navigasi Bawah
        navLaporan = findViewById(R.id.navLaporan);
        navProfil = findViewById(R.id.navProfil);
        // navHome tidak perlu di-init karena kita sudah di Home

        // Tombol Header Dokumentasi
        headerDokumentasi = findViewById(R.id.headerDokumentasi);
    }

    /**
     * Method untuk mendaftarkan aksi klik pada tombol
     */
    private void setupClickListeners() {

        // --- Listener untuk Tombol Layanan ---

        layananCekStatus.setOnClickListener(v -> {
            // Ganti Toast ini dengan Intent ke CekStatusActivity
            Toast.makeText(HomeActivity.this, "Membuka Cek Status Laporan...", Toast.LENGTH_SHORT).show();
            // Contoh: startActivity(new Intent(HomeActivity.this, CekStatusActivity.class));
        });

        layananArtikel.setOnClickListener(v -> {
            // Ganti Toast ini dengan Intent ke ArtikelActivity
            Toast.makeText(HomeActivity.this, "Membuka Artikel Edukasi...", Toast.LENGTH_SHORT).show();
            // Contoh: startActivity(new Intent(HomeActivity.this, ArtikelActivity.class));
        });

        layananInfoTPS.setOnClickListener(v -> {
            // Ganti Toast ini dengan Intent ke InfoTPSActivity
            Toast.makeText(HomeActivity.this, "Membuka Info TPS...", Toast.LENGTH_SHORT).show();
            // Contoh: startActivity(new Intent(HomeActivity.this, InfoTPSActivity.class));
        });


        // --- Listener untuk Header Dokumentasi ---

        headerDokumentasi.setOnClickListener(v -> {
            // Ganti Toast ini dengan Intent ke DokumentasiActivity
            Toast.makeText(HomeActivity.this, "Membuka semua dokumentasi...", Toast.LENGTH_SHORT).show();
            // Contoh: startActivity(new Intent(HomeActivity.this, DokumentasiActivity.class));
        });


        // --- Listener untuk Navigasi Bawah ---

        navLaporan.setOnClickListener(v -> {
            // Pindah ke PengajuanLaporanActivity
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class); // ⚠️ GANTI jika nama Activity beda
            startActivity(intent);
        });

        navProfil.setOnClickListener(v -> {
            // Pindah ke ProfilActivity
            Intent intent = new Intent(HomeActivity.this, RegisterActivity.class); // ⚠️ GANTI jika nama Activity beda
            startActivity(intent);
        });
    }
}