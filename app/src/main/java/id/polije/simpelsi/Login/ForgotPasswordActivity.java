package id.polije.simpelsi.Login;

import android.content.Context; // Masih diperlukan untuk Toast/Log
import android.content.Intent;
import android.os.Build; // Masih diperlukan untuk debug/logging
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Import untuk API, Model, dan Retrofit
import id.polije.simpelsi.R;
import id.polije.simpelsi.api.ApiClient;
import id.polije.simpelsi.api.ApiInterface;
import id.polije.simpelsi.model.OtpRequest;
import id.polije.simpelsi.model.OtpResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextView tvKembaliLogin;
    ImageView btnBack;
    Button btnKirim;
    EditText etEmail;
    ApiInterface apiInterface;

    // ❗️ Konstanta dan Launcher Notifikasi DIHAPUS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupakatasandi);

        // Inisialisasi
        tvKembaliLogin = findViewById(R.id.tv_kembali_login);
        btnBack = findViewById(R.id.btnBack);
        btnKirim = findViewById(R.id.btn_kirim);
        etEmail = findViewById(R.id.et_email_lupa_sandi);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        // ❗️ createNotificationChannel() DIHAPUS

        // Listener
        tvKembaliLogin.setOnClickListener(v -> finish());

        // Tombol Kirim (langsung panggil requestOtp)
        btnKirim.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(ForgotPasswordActivity.this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }
            requestOtp(email); // ❗️ askNotificationPermissionAndRequestOtp DIHAPUS
        });
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }


    private void requestOtp(String email) {
        Toast.makeText(ForgotPasswordActivity.this, "Mengirim permintaan...", Toast.LENGTH_SHORT).show();
        OtpRequest request = new OtpRequest(email);
        Call<OtpResponse> call = apiInterface.requestOtp(request);

        call.enqueue(new Callback<OtpResponse>() {
            @Override
            public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OtpResponse otpResponse = response.body();

                    if (otpResponse.isSuccess()) {
                        // SUKSES DARI API
                        Toast.makeText(ForgotPasswordActivity.this, otpResponse.getMessage(), Toast.LENGTH_SHORT).show();

                        // ❗️ Log debug OTP (Opsional, untuk tes saja)
                        String otpCode = otpResponse.getOtp();
                        if (otpCode != null) {
                            Log.d("OTP_DEBUG", "Kode OTP (Server Response): " + otpCode);
                        }

                        // Pindah ke VerificationActivity
                        Intent intent = new Intent(ForgotPasswordActivity.this, VerificationActivity.class);
                        intent.putExtra("USER_EMAIL", email);
                        startActivity(intent);

                    } else {
                        // GAGAL DARI API (Mailjet error, Email tidak ditemukan, dll)
                        Toast.makeText(ForgotPasswordActivity.this, otpResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    // GAGAL KONEKSI (Kode 404/500)
                    Toast.makeText(ForgotPasswordActivity.this, "Gagal terhubung. Kode: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OtpResponse> call, Throwable t) {
                // GAGAL JARINGAN
                Toast.makeText(ForgotPasswordActivity.this, "Koneksi Gagal: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("REQUEST_OTP_FAILURE", "Error: " + t.getMessage(), t);
            }
        });
    }
}