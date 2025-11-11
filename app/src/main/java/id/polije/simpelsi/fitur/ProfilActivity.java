package id.polije.simpelsi.fitur;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import id.polije.simpelsi.Login.LoginActivity;
import id.polije.simpelsi.R;

public class ProfilActivity extends AppCompatActivity {

    private ImageView backButton, profileImage;
    private TextView userName, userEmail, menuProfilKami, menuVisiMisi, menuWebsite;
    private MaterialButton logoutButton;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        // 🔹 Inisialisasi View dari XML
        backButton = findViewById(R.id.back_button);
        profileImage = findViewById(R.id.profile_image);
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        menuProfilKami = findViewById(R.id.menu_profil_kami);
        menuVisiMisi = findViewById(R.id.menu_visi_misi);
        menuWebsite = findViewById(R.id.menu_website);
        logoutButton = findViewById(R.id.logout_button);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        // 🔹 Ambil data user dari SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        String nama = prefs.getString("nama", "Nama tidak ditemukan");
        String email = prefs.getString("email", "Email tidak ditemukan");
        String fotoUrl = prefs.getString("foto", ""); // opsional kalau belum ada di API

        // 🔹 Tampilkan data profil
        userName.setText(nama);
        userEmail.setText(email);

        if (!fotoUrl.isEmpty()) {
            Glide.with(this)
                    .load(fotoUrl)
                    .placeholder(R.drawable.profil)
                    .into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.profil);
        }

        // 🔹 Tombol kembali
        backButton.setOnClickListener(v -> onBackPressed());

        // 🔹 Tombol menu "Profil Kami"
        menuProfilKami.setOnClickListener(v ->
                Toast.makeText(this, "Membuka Profil Kami", Toast.LENGTH_SHORT).show()
        );

        // 🔹 Tombol menu "Visi Misi"
        menuVisiMisi.setOnClickListener(v ->
                Toast.makeText(this, "Membuka Visi & Misi", Toast.LENGTH_SHORT).show()
        );

        // 🔹 Tombol menu "Website" - membuka browser
        menuWebsite.setOnClickListener(v -> {
            String url = "https://simpelsi.medianewsonline.com";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        });

        // 🔹 Tombol Logout
        logoutButton.setOnClickListener(v -> {
            Toast.makeText(this, "Anda telah keluar", Toast.LENGTH_SHORT).show();

            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(ProfilActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // 🔹 Setup Bottom Navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_profil);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(ProfilActivity.this, HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_pengajuan) {
                startActivity(new Intent(ProfilActivity.this, PengajuanLaporanActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_profil) {
                return true;
            }
            return false;
        });
    }
}
