package id.polije.simpelsi; // Pastikan package Anda benar

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log; // ❗️ Tambahkan import Log
import android.widget.Button;
import android.widget.EditText; // ❗️ Tambahkan import EditText
import android.widget.TextView;
import android.widget.Toast; // ❗️ Tambahkan import Toast

// ❗️ Tambahkan import untuk API dan Model
import id.polije.simpelsi.api.ApiClient;
import id.polije.simpelsi.api.ApiInterface; // ❗️ Ganti jika nama interface Anda beda (misal: ApiService)
import id.polije.simpelsi.model.LoginRequest;
import id.polije.simpelsi.model.LoginResponse;

// ❗️ Tambahkan import untuk Retrofit
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextView tvDaftar, tvLupaSandi, tvTitle;
    private Button btnMasuk, btnGoogle;

    // ❗️ Deklarasi EditText
    private EditText etEmail, etPassword;

    // ❗️ Deklarasi ApiInterface
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 🔹 Inisialisasi view
        tvDaftar = findViewById(R.id.tv_daftar);
        tvLupaSandi = findViewById(R.id.tv_lupa_sandi);
        tvTitle = findViewById(R.id.tv_title);
        btnMasuk = findViewById(R.id.btn_masuk);
        btnGoogle = findViewById(R.id.btn_google);

        // ❗️ Inisialisasi EditText
        // ⚠️ PASTIKAN ID INI (et_email & et_password) BENAR DI XML ANDA ⚠️
        etEmail = findViewById(R.id.et_email_login);
        etPassword = findViewById(R.id.et_password_login);

        // ❗️ Inisialisasi ApiInterface
        // Pastikan nama interface Anda (ApiInterface.class) sudah benar
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        // 🔹 Efek gradasi pada judul
        Shader shader = new LinearGradient(
                0, 0, 0, tvTitle.getTextSize(),
                new int[]{Color.parseColor("#388E3C"), Color.parseColor("#379683")},
                null,
                Shader.TileMode.CLAMP
        );
        tvTitle.getPaint().setShader(shader);

        // 🔹 Tombol "Daftar" → ke RegisterActivity
        tvDaftar.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // 🔹 Tombol "Lupa Sandi" → ke ForgotPasswordActivity
        tvLupaSandi.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // 🔹 Tombol "Masuk" → PERBAIKAN DI SINI
        btnMasuk.setOnClickListener(v -> {
            // Ambil teks dari EditText
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Validasi input
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Email dan password tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }

            // Panggil method login
            loginUser(email, password);
        });

        // 🔹 Tombol "Masuk dengan Google"
        btnGoogle.setOnClickListener(v -> {
            // TODO: Tambahkan logika login Google nanti
            Intent intent = new Intent(LoginActivity.this, VerificationActivity.class);
            startActivity(intent);
        });
    }

    /**
     * ❗️ METHOD BARU: Untuk memanggil API login
     */
    private void loginUser(String email, String password) {
        // Buat objek Request
        LoginRequest loginRequest = new LoginRequest(email, password);

        // Panggil API
        Call<LoginResponse> call = apiInterface.loginUser(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if (loginResponse.isSuccess()) {
                        // LOGIN BERHASIL
                        LoginResponse.UserData user = loginResponse.getData();
                        Toast.makeText(LoginActivity.this, "Login Berhasil! Selamat datang, " + user.getNama(), Toast.LENGTH_LONG).show();

                        // Pindah ke HomeActivity
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish(); // Tutup LoginActivity agar tidak bisa kembali

                    } else {
                        // LOGIN GAGAL (Email/password salah dari server)
                        Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Gagal terhubung (Misal: error 404, 500, atau server tidak ditemukan)
                    Toast.makeText(LoginActivity.this, "Gagal terhubung ke server. Kode: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Gagal koneksi (Internet mati atau MASALAH COOKIE BYETHOST)
                Toast.makeText(LoginActivity.this, "Koneksi Gagal: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("LOGIN_FAILURE", "Error: " + t.getMessage(), t);
            }
        });
    }
}