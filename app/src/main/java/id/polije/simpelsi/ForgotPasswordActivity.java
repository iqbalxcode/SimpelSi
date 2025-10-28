package id.polije.simpelsi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // ❗️ Import Log
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// ❗️ Import untuk API, Model, dan Retrofit
import id.polije.simpelsi.api.ApiClient;
import id.polije.simpelsi.api.ApiInterface;
import id.polije.simpelsi.model.OtpRequest;
import id.polije.simpelsi.model.OtpResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextView tvKembaliLogin;
    ImageButton btnBack;
    Button btnKirim;
    EditText etEmail;

    // ❗️ 1. Deklarasikan ApiInterface
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupakatasandi);

        // Inisialisasi komponen
        tvKembaliLogin = findViewById(R.id.tv_kembali_login);
        btnBack = findViewById(R.id.btn_back);
        btnKirim = findViewById(R.id.btn_kirim);
        etEmail = findViewById(R.id.et_email); // ⚠️ Pastikan ID ini benar di XML

        // ❗️ 2. Inisialisasi ApiInterface
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        // Aksi klik
        tvKembaliLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // ❗️ 3. OnClickListener untuk Tombol Kirim (DENGAN LOGIKA API)
        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Panggil method untuk meminta OTP
                requestOtp(email);
            }
        });
    }

    /**
     * ❗️ 4. Method baru untuk memanggil API request_otp.php
     */
    private void requestOtp(String email) {
        // Tampilkan loading
        Toast.makeText(ForgotPasswordActivity.this, "Mengirim permintaan...", Toast.LENGTH_SHORT).show();

        // Buat request body
        OtpRequest request = new OtpRequest(email);

        // Panggil API
        Call<OtpResponse> call = apiInterface.requestOtp(request);
        call.enqueue(new Callback<OtpResponse>() {
            @Override
            public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().isSuccess()) {
                        // SUKSES DARI API
                        Toast.makeText(ForgotPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();

                        // Pindah ke VerificationActivity
                        Intent intent = new Intent(ForgotPasswordActivity.this, VerificationActivity.class);

                        // (PENTING) Kirim email ke VerificationActivity
                        intent.putExtra("USER_EMAIL", email);
                        startActivity(intent);

                    } else {
                        // GAGAL DARI API (misal: email tidak terdaftar)
                        Toast.makeText(ForgotPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }

                } else {
                    // GAGAL KONEKSI (Error 404, 500, dll)
                    Toast.makeText(ForgotPasswordActivity.this, "Gagal terhubung ke server. Kode: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OtpResponse> call, Throwable t) {
                // GAGAL JARINGAN (Internet mati, dll)
                Toast.makeText(ForgotPasswordActivity.this, "Koneksi Gagal: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("REQUEST_OTP_FAILURE", "Error: " + t.getMessage(), t);
            }
        });
    }
}