package id.polije.simpelsi.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import id.polije.simpelsi.R;
import id.polije.simpelsi.api.ApiClient;
import id.polije.simpelsi.api.ApiInterface;
import id.polije.simpelsi.fitur.HomeActivity;
import id.polije.simpelsi.model.LoginRequest;
import id.polije.simpelsi.model.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextView tvDaftar, tvLupaSandi, btnGoogle;
    private Button btnMasuk;
    private EditText etEmail, etPassword;
    private ImageView ivTogglePassword;
    private boolean isPasswordVisible = false;

    private ApiInterface apiInterface;
    private GoogleSignInClient googleSignInClient;
    private boolean isGoogleLoading = false;

    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        Intent data = result.getData();
                        if (result.getResultCode() == RESULT_OK && data != null) {
                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                            try {
                                GoogleSignInAccount account = task.getResult(ApiException.class);
                                if (account != null) {
                                    String googleId = account.getId();
                                    String email = account.getEmail();
                                    String name = account.getDisplayName();
                                    loginWithGoogle(googleId, email, name);
                                }
                            } catch (ApiException e) {
                                resetGoogleButton();
                                Toast.makeText(this, "Login Google gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            resetGoogleButton();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 🔹 Cek apakah user sudah login
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        if (prefs.getBoolean("is_logged_in", false)) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
            return;
        }

        // --- Inisialisasi View ---
        tvDaftar = findViewById(R.id.tv_daftar);
        tvLupaSandi = findViewById(R.id.tv_lupa_sandi);
        btnMasuk = findViewById(R.id.btn_masuk);
        btnGoogle = findViewById(R.id.btn_google);
        etEmail = findViewById(R.id.et_email_login);
        etPassword = findViewById(R.id.et_password_login);
        ivTogglePassword = findViewById(R.id.iv_toggle_password);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        // --- Fitur Toggle Mata ---
        ivTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // 🔒 Sembunyikan password
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.of); // kembali ke icon mata tertutup
                isPasswordVisible = false;
            } else {
                // 👁️ Tampilkan password
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.eye); // ubah ke icon mata terbuka
                isPasswordVisible = true;
            }
            // Biar kursor tetap di akhir teks
            etPassword.setSelection(etPassword.getText().length());
        });

        // --- Tombol login manual ---
        btnMasuk.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            btnMasuk.setEnabled(false);
            btnMasuk.setText("Memproses...");
            loginUser(email, password);
        });

        // --- Tombol login Google ---
        btnGoogle.setOnClickListener(v -> {
            if (isGoogleLoading) return;
            isGoogleLoading = true;
            btnGoogle.setEnabled(false);
            btnGoogle.setText("Memproses...");

            googleSignInClient.signOut().addOnCompleteListener(task -> {
                googleSignInClient.revokeAccess().addOnCompleteListener(task2 -> {
                    Intent signInIntent = googleSignInClient.getSignInIntent();
                    googleSignInLauncher.launch(signInIntent);
                });
            });
        });

        // --- Navigasi ke halaman lain ---
        tvDaftar.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        tvLupaSandi.setOnClickListener(v -> startActivity(new Intent(this, ForgotPasswordActivity.class)));

        // --- Konfigurasi Google Sign-In ---
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void loginUser(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        Call<LoginResponse> call = apiInterface.loginUser(request);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                btnMasuk.setEnabled(true);
                btnMasuk.setText("Masuk");

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse res = response.body();
                    if (res.isSuccess() && res.getData() != null) {
                        LoginResponse.UserData user = res.getData();
                        saveUserSession(user.getId_masyarakat(), user.getNama(), user.getEmail());
                        goToHome();
                    } else {
                        Toast.makeText(LoginActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Login gagal: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnMasuk.setEnabled(true);
                btnMasuk.setText("Masuk");
                Toast.makeText(LoginActivity.this, "Kesalahan koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginWithGoogle(String googleId, String email, String nama) {
        Call<LoginResponse> call = apiInterface.loginGoogle(googleId, email, nama);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                resetGoogleButton();

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse res = response.body();

                    if (res.isSuccess() && res.getData() != null) {
                        LoginResponse.UserData user = res.getData();
                        saveUserSession(user.getId_masyarakat(), user.getNama(), user.getEmail());
                        Toast.makeText(LoginActivity.this, "Login Google berhasil", Toast.LENGTH_SHORT).show();
                        goToHome();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login gagal: " + res.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Login gagal ke server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                resetGoogleButton();
                Toast.makeText(LoginActivity.this, "Kesalahan koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserSession(String id, String nama, String email) {
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("id_masyarakat", id);
        editor.putString("nama", nama);
        editor.putString("email", email);
        editor.putBoolean("is_logged_in", true);
        editor.apply();
    }

    private void goToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void resetGoogleButton() {
        isGoogleLoading = false;
        btnGoogle.setEnabled(true);
        btnGoogle.setText("Masuk dengan akun Google");
    }
}