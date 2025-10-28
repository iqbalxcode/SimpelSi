package id.polije.simpelsi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.common.SignInButton;

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
    private TextView btnGoogle; // bisa diganti SignInButton kalau mau
    private EditText etEmail, etPassword;
    private ApiInterface apiInterface;

    private GoogleSignInClient googleSignInClient;
    private boolean isGoogleLoading = false;

    // ActivityResultLauncher untuk startActivityForResult (Google SignIn)
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
                                // Ambil token/id/email/nama
                                String idToken = account.getIdToken(); // kalau di-request
                                String googleId = account.getId();
                                String email = account.getEmail();
                                String name = account.getDisplayName();

                                // Panggil login ke server
                                loginWithGoogle(googleId, email, name, true);
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

        apiInterface = ApiClient.getService().create(ApiInterface.class);

        // Konfigurasi GoogleSignInOptions
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // mintalah ID token jika server membutuhkan idToken untuk verifikasi
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // klik daftar / lupa sandi
        tvDaftar.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        tvLupaSandi.setOnClickListener(v -> startActivity(new Intent(this, ForgotPasswordActivity.class)));

        // login manual
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

        // google sign in
        btnGoogle.setOnClickListener(v -> {
            if (isGoogleLoading) return;
            isGoogleLoading = true;
            btnGoogle.setEnabled(false);
            btnGoogle.setText("Memproses...");
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }

    // Manual login
    private void loginUser(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        Call<LoginResponse> call = apiInterface.loginUser(request);
        call.enqueue(new Callback<LoginResponse>() {
            @Override public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                btnMasuk.setEnabled(true);
                btnMasuk.setText("Masuk");
                handleLoginResponse(response);
            }
            @Override public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnMasuk.setEnabled(true);
                btnMasuk.setText("Masuk");
                Toast.makeText(LoginActivity.this, "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Kirim data google ke server
    private void loginWithGoogle(String googleId, String email, String name, boolean verified) {
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
    }

    private void resetGoogleButton() {
        isGoogleLoading = false;
        btnGoogle.setEnabled(true);
        btnGoogle.setText("Masuk dengan Google");
    }

    private void handleLoginResponse(Response<LoginResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            LoginResponse res = response.body();
            if ("success".equals(res.getStatus())) {
                LoginResponse.User user = res.getUser();
                saveUserSession(user.getId(), user.getNama(), user.getEmail());
                Toast.makeText(this, "Selamat datang, " + user.getNama() + "!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            } else {
                String msg = res.getMessage() != null ? res.getMessage() : "Login gagal";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserSession(String id, String nama, String email) {
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("id", id);
        editor.putString("nama", nama);
        editor.putString("email", email);
        editor.putBoolean("is_logged_in", true);
        editor.apply();
    }
}
