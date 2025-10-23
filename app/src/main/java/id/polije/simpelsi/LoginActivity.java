package id.polije.simpelsi;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private TextView tvDaftar, tvLupaSandi, tvTitle;
    private Button btnMasuk, btnGoogle;

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

        // 🔹 Tombol "Masuk" → nanti isi logika login
        btnMasuk.setOnClickListener(v -> {
            // TODO: Tambahkan logika login di sini
            // Contoh:
            // Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            // startActivity(intent);
            // finish();
        });

        // 🔹 Tombol "Masuk dengan Google" → ke VerificationActivity
        btnGoogle.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, VerificationActivity.class);
            startActivity(intent);
        });
    }
}
