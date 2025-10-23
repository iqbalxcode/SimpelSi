package id.polije.simpelsi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextView tvKembaliLogin;
    ImageButton btnBack;// deklarasi variabel

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupakatasandi);

        // Inisialisasi komponen
        tvKembaliLogin = findViewById(R.id.tv_kembali_login);
        btnBack = findViewById(R.id.btn_back);

        // Aksi klik
        tvKembaliLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Opsional: agar user tidak bisa kembali ke halaman lupa password
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // opsional: agar tidak bisa kembali ke halaman register
            }
        });
    }
}
