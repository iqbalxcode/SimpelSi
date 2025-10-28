package id.polije.simpelsi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.util.Log; // ❗️ Import Log

// ❗️ Import Retrofit dan Model
import id.polije.simpelsi.api.ApiClient;
import id.polije.simpelsi.api.ApiInterface;
import id.polije.simpelsi.model.RegisterRequest;
import id.polije.simpelsi.model.RegisterResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    Button btnKirim;
    ImageButton btnBack;

    private EditText etNama, etEmail, etPassword;

    // ❗️ 1. Deklarasikan ApiInterface
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inisialisasi komponen
        btnKirim = findViewById(R.id.btn_kirim_register);
        btnBack = findViewById(R.id.btn_back);

        etNama = findViewById(R.id.et_nama_register);
        etEmail = findViewById(R.id.et_email_register);
        etPassword = findViewById(R.id.et_password_register);

        // ❗️ 2. Inisialisasi ApiInterface
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        // ❗️ 3. Tombol kirim dengan logika API
        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ambil data dari form
                String nama = etNama.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Validasi dasar
                if (nama.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Semua data wajib diisi!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 8) {
                    Toast.makeText(RegisterActivity.this, "Password minimal 8 huruf", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Panggil method untuk registrasi
                performRegister(nama, email, password);
            }
        });

        // Tombol back ke halaman login
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * ❗️ 4. Method baru untuk memanggil API Registrasi
     */
    private void performRegister(String nama, String email, String password) {
        // Tampilkan loading (jika ada)
        Toast.makeText(RegisterActivity.this, "Registrasi sedang diproses...", Toast.LENGTH_SHORT).show();

        // Buat objek request
        RegisterRequest request = new RegisterRequest(nama, email, password);

        // Panggil API
        Call<RegisterResponse> call = apiInterface.registerUser(request);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();

                    if (registerResponse.isSuccess()) {
                        // REGISTRASI BERHASIL
                        Toast.makeText(RegisterActivity.this, registerResponse.getMessage(), Toast.LENGTH_LONG).show();

                        // Pindah ke Login
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // Tutup activity ini

                    } else {
                        // REGISTRASI GAGAL (misal: email sudah ada)
                        Toast.makeText(RegisterActivity.this, registerResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Gagal terhubung ke server
                    Toast.makeText(RegisterActivity.this, "Gagal terhubung ke server. Kode: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                // Gagal koneksi (internet mati, dll)
                Toast.makeText(RegisterActivity.this, "Koneksi Gagal: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("REGISTER_FAILURE", "Error: " + t.getMessage(), t);
            }
        });
    }
}