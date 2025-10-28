package id.polije.simpelsi; // ⚠️ Pastikan package Anda benar

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.Button; // ❗️ Import Button
import android.widget.EditText;
import android.widget.ImageButton; // ❗️ Import ImageButton
import android.widget.TextView;
import android.widget.Toast; // ❗️ Import Toast
import androidx.appcompat.app.AppCompatActivity;

public class VerificationActivity extends AppCompatActivity {

    EditText otp1, otp2, otp3, otp4;

    // ❗️ 1. Deklarasikan tombol
    ImageButton btnBack;
    Button btnKirim;
    TextView tvKirimUlang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifikasi);

        // Hubungkan dengan ID di XML
        otp1 = findViewById(R.id.otp_1);
        otp2 = findViewById(R.id.otp_2);
        otp3 = findViewById(R.id.otp_3);
        otp4 = findViewById(R.id.otp_4);

        // ❗️ 2. Hubungkan tombol dengan ID di XML
        btnBack = findViewById(R.id.btn_back);
        btnKirim = findViewById(R.id.btn_kirim);
        tvKirimUlang = findViewById(R.id.tv_kirim_ulang);

        setupOtpInputs();

        // ❗️ 3. Panggil method untuk setup listener
        setupClickListeners();
    }

    /**
     * ❗️ 4. Method baru untuk menangani semua klik
     */
    private void setupClickListeners() {
        // Tombol Kembali
        btnBack.setOnClickListener(v -> {
            finish(); // Tutup activity ini dan kembali ke sebelumnya
        });

        // ❗️ 5. TOMBOL KIRIM (SESUAI PERMINTAAN ANDA)
        btnKirim.setOnClickListener(v -> {
            // Ambil semua OTP
            String otp = otp1.getText().toString().trim() +
                    otp2.getText().toString().trim() +
                    otp3.getText().toString().trim() +
                    otp4.getText().toString().trim();

            // Validasi sederhana (Anda bisa tambahkan logika API di sini)
            if (otp.length() < 4) {
                Toast.makeText(this, "Kode OTP harus 4 digit", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: Panggil API 'verify_otp.php' Anda di sini

            // --- INI ADALAH BAGIAN YANG ANDA MINTA ---
            // Jika verifikasi (API) berhasil:
            Toast.makeText(this, "Verifikasi Berhasil!", Toast.LENGTH_SHORT).show();

            // Pindah ke NewPasswordActivity
            Intent intent = new Intent(VerificationActivity.this, NewPasswordActivity.class);

            // (PENTING) Kirim email ke activity berikutnya
            // intent.putExtra("USER_EMAIL", emailDariIntentSebelumnya);

            startActivity(intent);
            // --- AKHIR BAGIAN ---
        });

        // Tombol Kirim Ulang
        tvKirimUlang.setOnClickListener(v -> {
            // TODO: Panggil API 'request_otp.php' Anda lagi
            Toast.makeText(this, "Mengirim ulang kode OTP...", Toast.LENGTH_SHORT).show();
        });
    }

    // --- Kode Anda yang lain (sudah benar) ---

    private void setupOtpInputs() {
        // Pindah ke OTP berikutnya otomatis
        otp1.addTextChangedListener(new GenericTextWatcher(otp1, otp2));
        otp2.addTextChangedListener(new GenericTextWatcher(otp2, otp3));
        otp3.addTextChangedListener(new GenericTextWatcher(otp3, otp4));
        otp4.addTextChangedListener(new GenericTextWatcher(otp4, null));

        // Jika hapus (backspace) otomatis ke kolom sebelumnya
        otp2.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL && otp2.getText().toString().isEmpty()) {
                otp1.requestFocus();
                return true;
            }
            return false;
        });

        otp3.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL && otp3.getText().toString().isEmpty()) {
                otp2.requestFocus();
                return true;
            }
            return false;
        });

        otp4.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL && otp4.getText().toString().isEmpty()) {
                otp3.requestFocus();
                return true;
            }
            return false;
        });
    }

    // Kelas bantu untuk pindah otomatis
    private class GenericTextWatcher implements TextWatcher {
        private final EditText currentView;
        private final EditText nextView;

        public GenericTextWatcher(EditText currentView, EditText nextView) {
            this.currentView = currentView;
            this.nextView = nextView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 1 && nextView != null) {
                nextView.requestFocus();
            }
        }
    }
}