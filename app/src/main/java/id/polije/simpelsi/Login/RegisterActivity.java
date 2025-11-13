package id.polije.simpelsi.Login; // Menggunakan package utama

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.util.Log;
import id.polije.simpelsi.R;
import id.polije.simpelsi.api.ApiClient;

// Retrofit & Model
// import id.polije.simpelsi.R; // Import R tidak diperlukan di sini
import id.polije.simpelsi.api.ApiClient;
import id.polije.simpelsi.api.ApiInterface;
import id.polije.simpelsi.model.RegisterRequest;
import id.polije.simpelsi.model.RegisterResponse;
// Import LoginActivity dari package yang benar
import id.polije.simpelsi.Login.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private Button btnKirim;
    private ImageView btnBack;
    private EditText etNama, etEmail, etPassword;

    // Variabel class yang akan kita gunakan
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inisialisasi view
        btnKirim = findViewById(R.id.btn_kirim_register);
        btnBack = findViewById(R.id.btnBack);
        etNama = findViewById(R.id.et_nama_register);
        etEmail = findViewById(R.id.et_email_register);
        etPassword = findViewById(R.id.et_password_register);

        // --- ⬇️ INI PERBAIKAN UTAMANYA ⬇️ ---
        // Inisialisasi variabel class 'apiInterface', BUKAN variabel lokal baru
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        // --- ⬆️ AKHIR PERBAIKAN ⬆️ ---

        // Tombol Kirim
        btnKirim.setOnClickListener(v -> {
            String nama = etNama.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Validasi (Kode Anda sudah bagus)
            if (nama.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua data wajib diisi!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 8) {
                Toast.makeText(this, "Password minimal 8 karakter", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Format email tidak valid", Toast.LENGTH_SHORT).show();
                return;
            }

            // Nonaktifkan tombol (Kode Anda sudah bagus)
            btnKirim.setEnabled(false);
            btnKirim.setText("Memproses...");

            performRegister(nama, email, password);
        });

        // Tombol Back
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void performRegister(String nama, String email, String password) {
        RegisterRequest request = new RegisterRequest(nama, email, password);

        // Sekarang 'apiInterface' tidak lagi null
        Call<RegisterResponse> call = apiInterface.registerUser(request);

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                // Aktifkan kembali tombol (Ganti 'Daftar' menjadi 'Kirim' agar konsisten)
                btnKirim.setEnabled(true);
                btnKirim.setText("Kirim");

                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse res = response.body();

                    if (res.isSuccess()) { // Pastikan Anda punya method isSuccess() di RegisterResponse
                        Toast.makeText(RegisterActivity.this, res.getMessage(), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, res.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this,
                            "Gagal: " + response.code() + " - " + response.message(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                // Aktifkan kembali tombol
                btnKirim.setEnabled(true);
                btnKirim.setText("Kirim");

                Toast.makeText(RegisterActivity.this,
                        "Koneksi gagal: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e("REGISTER_FAILURE", "Error: " + t.getMessage(), t);
            }
        });
    }
}