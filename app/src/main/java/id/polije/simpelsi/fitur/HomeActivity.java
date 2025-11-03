package id.polije.simpelsi.fitur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import id.polije.simpelsi.CekStatusLaporan.CekStatusLaporanActivity;
import id.polije.simpelsi.R;
import id.polije.simpelsi.artikel.ArtikelActivity;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout layananCekStatus, layananArtikel, layananInfoTPS;
    private LinearLayout headerDokumentasi;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        setupClickListeners();
        setupBottomNav();
    }

    /**
     * Inisialisasi semua view dari XML
     */
    private void initViews() {
        layananCekStatus = findViewById(R.id.layananCekStatus);
        layananArtikel = findViewById(R.id.layananArtikel);
        layananInfoTPS = findViewById(R.id.layananInfoTPS);
        headerDokumentasi = findViewById(R.id.trendingArtikelContainer);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    /**
     * Listener untuk klik fitur layanan & dokumentasi
     */
    private void setupClickListeners() {
        layananCekStatus.setOnClickListener(v -> {
            startActivity(new Intent(this, CekStatusLaporanActivity.class));
        });

        layananArtikel.setOnClickListener(v -> {
            startActivity(new Intent(this, ArtikelActivity.class));
        });

        layananInfoTPS.setOnClickListener(v -> {
            Toast.makeText(this, "Membuka Info TPS...", Toast.LENGTH_SHORT).show();
        });

        headerDokumentasi.setOnClickListener(v -> {
            Toast.makeText(this, "Membuka semua dokumentasi...", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Setup Bottom Navigation agar bisa digunakan lintas Activity
     */
    private void setupBottomNav() {
        if (bottomNavigationView == null) return;

        // ✅ Tidak perlu inflateMenu lagi, karena sudah diset di XML dengan:
        // app:menu="@menu/bottom_nav_menu"

        // Tandai halaman ini sebagai aktif
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    return true;

                } else if (id == R.id.nav_pengajuan) {
                    startActivity(new Intent(HomeActivity.this, PengajuanLaporanActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;

                } else if (id == R.id.nav_profil) {
                    startActivity(new Intent(HomeActivity.this, ProfilActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                }

                return false;
            }
        });
    }
}
