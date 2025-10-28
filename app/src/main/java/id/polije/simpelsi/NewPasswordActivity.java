package id.polije.simpelsi; // ⚠️ Sesuaikan package Anda

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent; // ❗️ Import Intent
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

// ... (Anda akan butuh import Retrofit di sini nanti)

public class NewPasswordActivity extends AppCompatActivity {

    ImageButton btnBack;
    Button btnKirim;
    EditText etNewPassword, etConfirmPassword;

    // ❗️ Anda perlu email pengguna dari halaman sebelumnya
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password); // ⚠️ Gunakan layout baru

        // ❗️ (Disarankan) Ambil email dari Intent
        // userEmail = getIntent().getStringExtra("USER_EMAIL");
        // if (userEmail == null) {
        //     Toast.makeText(this, "Email tidak ditemukan, silakan ulangi", Toast.LENGTH_LONG).show();
        //     finish();
        // }

        // Inisialisasi komponen
        btnBack = findViewById(R.id.btn_back_new_pass);
        btnKirim = findViewById(R.id.btn_kirim_new_pass);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);

        // Tombol kembali
        btnBack.setOnClickListener(v -> {
            finish(); // Cukup tutup halaman ini
        });

        // Tombol Kirim
        btnKirim.setOnClickListener(v -> {
            // Panggil method validasi
            validateAndSubmit();
        });
    }

    private void validateAndSubmit() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // 1. Validasi Kosong
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Validasi Panjang
        if (newPassword.length() < 8) {
            Toast.makeText(this, "Password minimal 8 huruf", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Validasi Kecocokan
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Password tidak cocok!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 4. Jika semua valid, kirim ke server
        Toast.makeText(this, "Mengirim password baru...", Toast.LENGTH_SHORT).show();

        // TODO: Panggil API 'reset_password.php' Anda di sini
        // Gunakan Retrofit untuk mengirim 'userEmail' dan 'newPassword'
        // ... (logika Retrofit) ...

        // --- ⬇️ INI PERBAIKANNYA ⬇️ ---
        // Asumsikan API sukses:

        Toast.makeText(this, "Password berhasil diubah!", Toast.LENGTH_LONG).show();

        // Pindah ke LoginActivity
        Intent intent = new Intent(NewPasswordActivity.this, LoginActivity.class);

        // Bersihkan stack activity agar tidak bisa kembali ke halaman ini
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Tutup activity ini
        // --- ⬆️ AKHIR PERBAIKAN ⬆️ ---
    }
}