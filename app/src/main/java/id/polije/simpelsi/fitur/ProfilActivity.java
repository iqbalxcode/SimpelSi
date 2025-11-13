package id.polije.simpelsi.fitur;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import id.polije.simpelsi.Login.LoginActivity;
import id.polije.simpelsi.R;

public class ProfilActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView userName, userEmail, menuProfilKami, menuVisiMisi, menuWebsite;
    private MaterialButton logoutButton;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        // 🔹 Inisialisasi View
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
        String nama = prefs.getString("nama", "");
        String email = prefs.getString("email", "");
        String fotoUrl = prefs.getString("photoUrl", "");

        // 🔹 Cek login dengan Google
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            nama = account.getDisplayName();
            email = account.getEmail();

            if (account.getPhotoUrl() != null) {
                fotoUrl = account.getPhotoUrl().toString();
            }
        }

        // 🔹 Tampilkan data profil
        updateUI(nama, email, fotoUrl);

        // 🔹 Tombol menu "Profil Kami"
        menuProfilKami.setOnClickListener(v -> openWebsite("https://dlhnganjuk.co.id/profile/tentang/"));
        menuVisiMisi.setOnClickListener(v -> openWebsite("https://dlhnganjuk.co.id/profile/tugas-dan-fungsi/"));
        menuWebsite.setOnClickListener(v -> openWebsite("https://dlhnganjuk.co.id"));

        // 🔹 Tombol Logout
        logoutButton.setOnClickListener(v -> {
            Toast.makeText(this, "Anda telah keluar", Toast.LENGTH_SHORT).show();

            // Hapus sesi lokal
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            // Logout juga dari Google
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this,
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                            .build());
            googleSignInClient.signOut();

            // Kembali ke login
            Intent intent = new Intent(ProfilActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // 🔹 Bottom Navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_profil);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_pengajuan) {
                startActivity(new Intent(this, PengajuanLaporanActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_profil) {
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshGoogleProfile();
    }

    private void refreshGoogleProfile() {
        GoogleSignInAccount lastAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (lastAccount == null) return;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInClient.silentSignIn()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        GoogleSignInAccount refreshedAccount = task.getResult();
                        if (refreshedAccount != null) {
                            String newName = refreshedAccount.getDisplayName();
                            String newEmail = refreshedAccount.getEmail();
                            String newPhotoUrl = refreshedAccount.getPhotoUrl() != null
                                    ? refreshedAccount.getPhotoUrl().toString()
                                    : "";

                            // 🔹 Simpan ulang ke SharedPreferences
                            SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("nama", newName);
                            editor.putString("email", newEmail);
                            editor.putString("photoUrl", newPhotoUrl);
                            editor.apply();

                            // 🔹 Update UI
                            updateUI(newName, newEmail, newPhotoUrl);
                        }
                    }
                });
    }

    private void updateUI(String nama, String email, String fotoUrl) {
        userName.setText(nama.isEmpty() ? "Nama tidak ditemukan" : nama);
        userEmail.setText(email.isEmpty() ? "Email tidak ditemukan" : email);

        if (!fotoUrl.isEmpty()) {
            Glide.with(this)
                    .load(fotoUrl)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .circleCrop()
                    .placeholder(R.drawable.profil)
                    .into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.profil);
        }
    }

    private void openWebsite(String url) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Tidak dapat membuka browser", Toast.LENGTH_SHORT).show();
        }
    }
}
