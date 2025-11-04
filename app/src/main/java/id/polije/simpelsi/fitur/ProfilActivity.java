package id.polije.simpelsi.fitur;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent; // ❗️ Import Intent
import android.content.SharedPreferences; // ❗️ Import SharedPreferences
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import id.polije.simpelsi.Login.LoginActivity;
import id.polije.simpelsi.R;

public class ProfilActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView menuProfilKami;
    private TextView menuVisiMisi;
    private TextView menuWebsite;
    private Button logoutButton;
    private BottomNavigationView bottomNavigationView;
    private TextView userNameText, userEmailText;


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

        // Tambahan inisialisasi TextView nama & email
        userNameText = findViewById(R.id.user_name);
        userEmailText = findViewById(R.id.user_email);

        // Ambil data dari SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        String nama = prefs.getString("nama", "Nama tidak ditemukan");
        String email = prefs.getString("email", "Email tidak ditemukan");

        // Tampilkan ke TextView
        userNameText.setText(nama);
        userEmailText.setText(email);

        // Setup Bottom Navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_profil);

        // --- ⬇️ PERBAIKAN 1: Navigasi Bawah ⬇️ ---
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Navigasi ke Home
                startActivity(new Intent(ProfilActivity.this, HomeActivity.class));
                overridePendingTransition(0, 0); // Transisi instan
                finish(); // Tutup activity ini
                return true;
            } else if (itemId == R.id.nav_pengajuan) {
                // Navigasi ke Pengajuan Laporan
                startActivity(new Intent(ProfilActivity.this, PengajuanLaporanActivity.class));
                overridePendingTransition(0, 0); // Transisi instan
                finish(); // Tutup activity ini
                return true;
            } else if (itemId == R.id.nav_profil) {
                // Sudah di Profil
                return true;
            }
            return false;
        });
        // --- ⬆️ AKHIR PERBAIKAN 1 ⬆️ ---

        // Setup Listener untuk tombol Kembali
        backButton.setOnClickListener(v -> {
            onBackPressed(); // (Sudah benar)
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
            // Buka link website di browser
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.simpelsi.medianewsonline.com"));
            startActivity(browserIntent);
        });

        // --- ⬇️ PERBAIKAN 2: Tombol Logout ⬇️ ---
        logoutButton.setOnClickListener(v -> {
            // Tampilkan Toast
            Toast.makeText(this, "Anda Telah Keluar", Toast.LENGTH_SHORT).show();

            // Hapus data sesi dari SharedPreferences
            // Hapus sesi login
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            // Kembali ke LoginActivity
            Intent intent = new Intent(ProfilActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // Tutup ProfilActivity
        });
        // --- ⬆️ AKHIR PERBAIKAN 2 ⬆️ ---
    }
}