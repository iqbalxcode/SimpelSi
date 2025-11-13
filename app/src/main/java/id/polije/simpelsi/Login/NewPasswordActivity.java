package id.polije.simpelsi.Login; // ⚠️ Sesuaikan package Anda

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // ❗️ Import Log
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

// ❗️ Import untuk API, Model, dan Retrofit
import id.polije.simpelsi.R;
import id.polije.simpelsi.api.ApiClient;
import id.polije.simpelsi.api.ApiInterface;
import id.polije.simpelsi.model.ResetRequest;
import id.polije.simpelsi.model.ResetResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewPasswordActivity extends AppCompatActivity {

    ImageView btnBack;
    Button btnKirim;
    EditText etNewPassword, etConfirmPassword;

    // ❗️ Variabel untuk menyimpan data dari Intent
    String userEmail;
    String userOtp; // Anda juga perlu OTP dari halaman verifikasi

    // ❗️ Deklarasi ApiInterface
    ApiInterface apiInterface; // Variabel ini yang akan kita gunakan

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password); // ⚠️ Gunakan layout baru

        // ❗️ Ambil email DAN OTP dari Intent (dikirim dari VerificationActivity)
        userEmail = getIntent().getStringExtra("USER_EMAIL");
        userOtp = getIntent().getStringExtra("USER_OTP"); // ❗️ Pastikan VerificationActivity mengirim ini

        // Validasi data intent
        if (userEmail == null || userOtp == null) {
            Toast.makeText(this, "Sesi tidak valid, silakan ulangi", Toast.LENGTH_LONG).show();
            finish();
            return; // Hentikan eksekusi jika data tidak ada
        }

        // Inisialisasi komponen
        btnBack = findViewById(R.id.btnBack);
        btnKirim = findViewById(R.id.btn_kirim_new_pass);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);

        // ❗️ Inisialisasi ApiInterface (PERBAIKAN DI SINI)
        // Kita inisialisasi variabel 'apiInterface' yang ada di level kelas
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

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

        // --- ⬇️ INI PERBAIKANNYA (Logika API) ⬇️ ---

        // Panggil API 'reset_password.php'
        ResetRequest request = new ResetRequest(userEmail, userOtp, newPassword);

        // Sekarang 'apiInterface' tidak lagi null
        Call<ResetResponse> call = apiInterface.resetPassword(request);

        call.enqueue(new Callback<ResetResponse>() {
            @Override
            public void onResponse(Call<ResetResponse> call, Response<ResetResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().isSuccess()) {
                        // SUKSES DARI API
                        Toast.makeText(NewPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();

                        // Pindah ke LoginActivity
                        Intent intent = new Intent(NewPasswordActivity.this, LoginActivity.class);
                        // Bersihkan stack activity
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish(); // Tutup activity ini

                    } else {
                        // GAGAL DARI API (misal: OTP salah, dll)
                        Toast.makeText(NewPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }

                } else {
                    // GAGAL KONEKSI (Error 404, 500, dll)
                    Toast.makeText(NewPasswordActivity.this, "Gagal terhubung ke server. Kode: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResetResponse> call, Throwable t) {
                // GAGAL JARINGAN (Internet mati, dll)
                Toast.makeText(NewPasswordActivity.this, "Koneksi Gagal: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("RESET_PASS_FAILURE", "Error: " + t.getMessage(), t);
            }
        });
        // --- ⬆️ AKHIR PERBAIKAN ⬆️ ---
    }
}