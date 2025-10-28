package id.polije.simpelsi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button; // ❗️ Import Button
import android.widget.EditText; // ❗️ Import EditText
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast; // ❗️ Import Toast

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextView tvKembaliLogin;
    ImageButton btnBack;

    // ❗️ 1. Deklarasikan Tombol Kirim dan EditText
    Button btnKirim;
    EditText etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupakatasandi); // ⚠️ Pastikan layout ini punya btn_kirim & et_email

        // Inisialisasi komponen
        tvKembaliLogin = findViewById(R.id.tv_kembali_login);
        btnBack = findViewById(R.id.btn_back);

        // ❗️ 2. Inisialisasi komponen baru
        // ⚠️ Pastikan ID ini sesuai dengan yang ada di file XML Anda
        btnKirim = findViewById(R.id.btn_kirim);
        etEmail = findViewById(R.id.et_email);

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

        // ❗️ 3. Tambahkan OnClickListener untuk Tombol Kirim
        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();

                // Validasi sederhana
                if (email.isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    return;
                }

                // TODO: Panggil API 'request_otp.php' Anda di sini
                // Kirim 'email' ini ke server untuk mendapatkan OTP
                //
                // Jika API sukses (pura-pura sukses untuk sekarang):
                Toast.makeText(ForgotPasswordActivity.this, "Mengirim kode OTP...", Toast.LENGTH_SHORT).show();

                // Pindah ke VerificationActivity
                Intent intent = new Intent(ForgotPasswordActivity.this, VerificationActivity.class);

                // (PENTING) Kirim email ke VerificationActivity
                // Ini dibutuhkan agar VerificationActivity tahu email siapa yg harus diverifikasi
                intent.putExtra("USER_EMAIL", email);

                startActivity(intent);
            }
        });
    }
}