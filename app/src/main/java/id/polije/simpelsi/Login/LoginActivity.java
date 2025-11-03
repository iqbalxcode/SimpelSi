package id.polije.simpelsi.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

import id.polije.simpelsi.fitur.HomeActivity;
import id.polije.simpelsi.R;
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

    private GoogleSignInClient googleSignInClient;
    private boolean isGoogleLoading = false;

    // ActivityResultLauncher untuk Google SignIn
    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        int resultCode = result.getResultCode();
                        Intent data = result.getData();
                        if (resultCode == RESULT_OK && data != null) {
                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                            try {
                                GoogleSignInAccount account = task.getResult(ApiException.class);
                                String idToken = account.getIdToken();
                                String googleId = account.getId();
                                String email = account.getEmail();
                                String name = account.getDisplayName();

                                // ❗️ ERROR: Ini akan crash kecuali Anda memperbaiki ApiInterface & LoginRequest
                                // loginWithGoogle(googleId, email, name, true);
                                Toast.makeText(this, "Fitur Google Login belum terhubung ke server.", Toast.LENGTH_LONG).show();
                                resetGoogleButton(); // Reset tombol
                            } catch (ApiException e) {
                                resetGoogleButton();
                                Toast.makeText(this, "Google sign in failed: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
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

        // Inisialisasi view
        tvDaftar = findViewById(R.id.tv_daftar);
        tvLupaSandi = findViewById(R.id.tv_lupa_sandi);
        btnMasuk = findViewById(R.id.btn_masuk);
        btnGoogle = findViewById(R.id.btn_google);
        etEmail = findViewById(R.id.et_email_login);
        etPassword = findViewById(R.id.et_password_login);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        // Google Sign In config
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        tvDaftar.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        tvLupaSandi.setOnClickListener(v -> startActivity(new Intent(this, ForgotPasswordActivity.class)));

        btnMasuk.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Format email tidak valid", Toast.LENGTH_SHORT).show();
                return;
            }

            btnMasuk.setEnabled(false);
            btnMasuk.setText("Memproses...");
            loginUser(email, password);
        });

        // tombol Google login
        btnGoogle.setOnClickListener(v -> {
            if (isGoogleLoading) return;
            isGoogleLoading = true;
            btnGoogle.setEnabled(false);
            btnGoogle.setText("Memproses...");
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }

    private void loginUser(String email, String password) {
        if (apiInterface == null) {
            Toast.makeText(this, "Gagal inisialisasi koneksi server", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest request = new LoginRequest(email, password);
        Call<LoginResponse> call = apiInterface.loginUser(request);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                btnMasuk.setEnabled(true);
                btnMasuk.setText("Masuk");

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse res = response.body();

                    // ❗️ PERBAIKAN: Sesuaikan dengan model LoginResponse.java Anda
                    if (res.isSuccess()) { // Menggunakan isSuccess()

                        LoginResponse.UserData user = res.getData(); // Menggunakan getData() dan UserData

                        // Pastikan user tidak null
                        if (user == null) {
                            Toast.makeText(LoginActivity.this, "Gagal mendapatkan data user.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Simpan sesi pengguna
                        saveUserSession(user.getId_masyarakat(), user.getNama(), user.getEmail()); // Menggunakan getter yang benar

                        Toast.makeText(LoginActivity.this,
                                "Selamat datang, " + user.getNama(),
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this,
                                res.getMessage() != null ? res.getMessage() : "Login gagal",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Login gagal: " + response.message(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnMasuk.setEnabled(true);
                btnMasuk.setText("Masuk");
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // ❗️ ERROR: Method ini tidak akan berfungsi sampai Anda:
    // 1. Menambahkan constructor baru ke LoginRequest.java
    // 2. Menambahkan method loginGoogle() ke ApiInterface.java
    // 3. Membuat file login_google.php di server Anda
    private void loginWithGoogle(String googleId, String email, String name, boolean verified) {
        /*
        LoginRequest request = new LoginRequest(googleId, email, name, verified);
        apiInterface.loginGoogle(request).enqueue(new Callback<LoginResponse>() {
            @Override public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                resetGoogleButton();
                handleLoginResponse(response);
            }
            @Override public void onFailure(Call<LoginResponse> call, Throwable t) {
                resetGoogleButton();
                Toast.makeText(LoginActivity.this, "Server error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        */
    }

    private void resetGoogleButton() {
        isGoogleLoading = false;
        btnGoogle.setEnabled(true);
        btnGoogle.setText("Masuk dengan akun Google");
    }

    // ❗️ ERROR: Method ini juga bergantung pada model `User` yang tidak sesuai
    private void handleLoginResponse(Response<LoginResponse> response) {
        /*
        if (response.isSuccessful() && response.body() != null) {
            LoginResponse res = response.body();
            if ("success".equals(res.getStatus())) { // Seharusnya res.isSuccess()
                LoginResponse.User user = res.getUser(); // Seharusnya res.getData()
                saveUserSession(user.getId(), user.getNama(), user.getEmail()); // Seharusnya user.getId_masyarakat()
                Toast.makeText(this, "Selamat datang, " + user.getNama() + "!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            } else { ... }
        } else { ... }
        */
    }

    private void saveUserSession(String id, String nama, String email) {
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // --- ⬇️ INI PERBAIKAN KRITIS UNTUK ERROR ANDA ⬇️ ---
        editor.putString("id_masyarakat", id); // ❗️ Ganti "id" menjadi "id_masyarakat"
        // --- ⬆️ AKHIR PERBAIKAN ⬆️ ---

        editor.putString("nama", nama);
        editor.putString("email", email);
        editor.putBoolean("is_logged_in", true);
        editor.apply();
    }
}