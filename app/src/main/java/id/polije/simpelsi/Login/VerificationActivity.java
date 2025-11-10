package id.polije.simpelsi.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable; // ❗️ Import
import android.text.TextWatcher; // ❗️ Import
import android.util.Log; // ❗️ Import
import android.view.KeyEvent; // ❗️ Import
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Import untuk API, Model, dan Retrofit
import id.polije.simpelsi.R;
import id.polije.simpelsi.api.ApiClient;
import id.polije.simpelsi.api.ApiInterface;
import id.polije.simpelsi.model.OtpRequest;
import id.polije.simpelsi.model.OtpResponse;
import id.polije.simpelsi.model.VerifyRequest; // ❗️ Import
import id.polije.simpelsi.model.VerifyResponse; // ❗️ Import
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationActivity extends AppCompatActivity {

    EditText otp1, otp2, otp3, otp4;
    ImageButton btnBack;
    Button btnKirim;
    TextView tvKirimUlang;

    // ❗️ Variabel sudah dideklarasikan
    private ApiInterface apiInterface;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifikasi);

        // Ambil email dari Intent (WAJIB)
        userEmail = getIntent().getStringExtra("USER_EMAIL");
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Terjadi kesalahan, email tidak ditemukan.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Hubungkan dengan ID di XML
        otp1 = findViewById(R.id.otp_1); // ❗️ Pastikan ID ini ada di XML
        otp2 = findViewById(R.id.otp_2);
        otp3 = findViewById(R.id.otp_3);
        otp4 = findViewById(R.id.otp_4);
        btnBack = findViewById(R.id.btn_back);
        btnKirim = findViewById(R.id.btn_kirim);
        tvKirimUlang = findViewById(R.id.tv_kirim_ulang);

        // --- ⬇️ PERBAIKAN: Menambahkan inisialisasi API ⬇️ ---
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        // --- ⬆️ AKHIR PERBAIKAN ⬆️ ---

        setupOtpInputs();
        setupClickListeners();
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnKirim.setOnClickListener(v -> {
            String otp = otp1.getText().toString().trim() +
                    otp2.getText().toString().trim() +
                    otp3.getText().toString().trim() +
                    otp4.getText().toString().trim();

            if (otp.length() < 4) {
                Toast.makeText(this, "Kode OTP harus 4 digit", Toast.LENGTH_SHORT).show();
                return;
            }
            verifyOtp(userEmail, otp);
        });

        tvKirimUlang.setOnClickListener(v -> {
            resendOtp(userEmail);
        });
    }

    private void verifyOtp(String email, String otp) {
        Toast.makeText(this, "Memverifikasi...", Toast.LENGTH_SHORT).show();

        VerifyRequest request = new VerifyRequest(email, otp);
        Call<VerifyResponse> call = apiInterface.verifyOtp(request); // Baris 111

        call.enqueue(new Callback<VerifyResponse>() {
            @Override
            public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(VerificationActivity.this, "Verifikasi Berhasil!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(VerificationActivity.this, NewPasswordActivity.class);
                        intent.putExtra("USER_EMAIL", email);
                        intent.putExtra("USER_OTP", otp);
                        startActivity(intent);
                    } else {
                        Toast.makeText(VerificationActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(VerificationActivity.this, "Gagal terhubung. Kode: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<VerifyResponse> call, Throwable t) {
                Toast.makeText(VerificationActivity.this, "Koneksi Gagal: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("VERIFY_OTP_FAILURE", "Error: " + t.getMessage(), t);
            }
        });
    }

    private void resendOtp(String email) {
        Toast.makeText(this, "Mengirim ulang kode OTP...", Toast.LENGTH_SHORT).show();

        OtpRequest request = new OtpRequest(email);
        Call<OtpResponse> call = apiInterface.requestOtp(request);

        call.enqueue(new Callback<OtpResponse>() {
            @Override
            public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Hanya perlu menampilkan pesan sukses/gagal dari server
                    Toast.makeText(VerificationActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    // Jika Anda ingin menampilkan OTP di notifikasi, tambahkan logika di sini
                } else {
                    Toast.makeText(VerificationActivity.this, "Gagal meminta OTP baru.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<OtpResponse> call, Throwable t) {
                Toast.makeText(VerificationActivity.this, "Koneksi Gagal: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    // --- ⬇️ Kode setupOtpInputs dan GenericTextWatcher Anda (disertakan kembali) ⬇️ ---

    private void setupOtpInputs() {
        otp1.addTextChangedListener(new GenericTextWatcher(otp1, otp2));
        otp2.addTextChangedListener(new GenericTextWatcher(otp2, otp3));
        otp3.addTextChangedListener(new GenericTextWatcher(otp3, otp4));
        otp4.addTextChangedListener(new GenericTextWatcher(otp4, null));

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
    // --- ⬆️ Akhir Kode Helper ⬆️ ---
}