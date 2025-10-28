package id.polije.simpelsi;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

// Retrofit
import id.polije.simpelsi.api.ApiClient;
import id.polije.simpelsi.api.ApiInterface;
import id.polije.simpelsi.model.LoginRequest;
import id.polije.simpelsi.model.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextView tvDaftar, tvLupaSandi;
    private Button btnMasuk;
    private TextView btnGoogle;

    private EditText etEmail, etPassword;
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 🔹 Inisialisasi view
        tvDaftar = findViewById(R.id.tv_daftar);
        tvLupaSandi = findViewById(R.id.tv_lupa_sandi);
        btnMasuk = findViewById(R.id.btn_masuk);
        btnGoogle = findViewById(R.id.btn_google);
        etEmail = findViewById(R.id.et_email_login);
        etPassword = findViewById(R.id.et_password_login);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        // 🔹 Klik "Daftar"
        tvDaftar.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        // 🔹 Klik "Lupa Sandi"
        tvLupaSandi.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });

        // 🔹 Klik "Masuk"
        btnMasuk.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(email, password);
        });

        // 🔹 Klik "Masuk dengan Google"
        btnGoogle.setOnClickListener(v -> {
            // nanti tambahkan login google di sini
            Intent intent = new Intent(LoginActivity.this, VerificationActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        Call<LoginResponse> call = apiInterface.loginUser(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if (loginResponse.isSuccess()) {
                        LoginResponse.UserData user = loginResponse.getData();
                        Toast.makeText(LoginActivity.this, "Login Berhasil! Selamat datang, " + user.getNama(), Toast.LENGTH_LONG).show();

                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Gagal terhubung ke server. Kode: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Koneksi Gagal: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("LOGIN_FAILURE", "Error: " + t.getMessage(), t);
            }
        });
    }
}
