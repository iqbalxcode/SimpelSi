package id.polije.simpelsi;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    TextView tvDaftar, tvLupaSandi;
    Button btnMasuk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvDaftar = findViewById(R.id.tv_daftar);
        tvLupaSandi = findViewById(R.id.tv_lupa_sandi);
        btnMasuk = findViewById(R.id.btn_masuk);

        tvDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pindah ke RegisterActivity
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        tvLupaSandi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pindah ke ForgotPasswordActivity
                Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(i);
            }
        });
        TextView textView = findViewById(R.id.tv_title);
        Shader shader = new LinearGradient(
                0, 0, 0, textView.getTextSize(),
                new int[]{Color.parseColor("#388E3C"), Color.parseColor("#379683")},
                null, Shader.TileMode.CLAMP);
        textView.getPaint().setShader(shader);


        btnMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logika login di sini
                // Jika sukses, pindah ke MainActivity (Halaman utama)
                // Intent i = new Intent(LoginActivity.this, MainActivity.class);
                // startActivity(i);
                // finish();
            }
        });
    }
}
