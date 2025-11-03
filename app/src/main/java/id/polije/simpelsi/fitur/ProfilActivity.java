package id.polije.simpelsi.fitur;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import id.polije.simpelsi.R;

public class ProfilActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView menuProfilKami;
    private TextView menuVisiMisi;
    private TextView menuWebsite;
    private Button logoutButton;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        // Inisialisasi Views
        backButton = findViewById(R.id.back_button);
        menuProfilKami = findViewById(R.id.menu_profil_kami);
        menuVisiMisi = findViewById(R.id.menu_visi_misi);
        menuWebsite = findViewById(R.id.menu_website);
        logoutButton = findViewById(R.id.logout_button);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        // Setup Bottom Navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_profil); // Asumsi ID menu profil adalah nav_profil

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Navigasi ke Home
                Toast.makeText(this, "Ke Halaman Home", Toast.LENGTH_SHORT).show();
                // Contoh: startActivity(new Intent(ProfilActivity.this, HomeActivity.class));
                return true;
            } else if (itemId == R.id.nav_pengajuan) {
                // Navigasi ke Pengajuan Laporan
                Toast.makeText(this, "Ke Pengajuan Laporan", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_profil) {
                // Sudah di Profil
                return true;
            }
            return false;
        });

        // Setup Listener untuk tombol Kembali
        backButton.setOnClickListener(v -> {
            onBackPressed(); // Kembali ke layar sebelumnya
        });

        // Setup Listener untuk menu-menu
        menuProfilKami.setOnClickListener(v -> {
            Toast.makeText(this, "Membuka Profil Kami", Toast.LENGTH_SHORT).show();
            // Implementasi intent untuk membuka halaman Profil Kami
        });

        menuVisiMisi.setOnClickListener(v -> {
            Toast.makeText(this, "Membuka Visi & Misi", Toast.LENGTH_SHORT).show();
            // Implementasi intent untuk membuka halaman Visi & Misi
        });

        menuWebsite.setOnClickListener(v -> {
            Toast.makeText(this, "Membuka Website", Toast.LENGTH_SHORT).show();
            // Implementasi intent untuk membuka browser ke URL website
        });

        // Setup Listener untuk tombol Keluar
        logoutButton.setOnClickListener(v -> {
            // Logika untuk proses Logout
            Toast.makeText(this, "Anda Telah Keluar", Toast.LENGTH_SHORT).show();
            // Contoh: Hapus token sesi dan kembali ke LoginActivity
            // Intent intent = new Intent(ProfilActivity.this, LoginActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // startActivity(intent);
        });
    }
}