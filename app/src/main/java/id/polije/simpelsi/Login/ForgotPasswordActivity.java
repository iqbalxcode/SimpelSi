package id.polije.simpelsi.Login;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log; // ❗️ Pastikan Log di-import
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

// Import untuk API, Model, dan Retrofit
import id.polije.simpelsi.R;
import id.polije.simpelsi.api.ApiClient;
import id.polije.simpelsi.api.ApiInterface;
import id.polije.simpelsi.model.OtpRequest;
import id.polije.simpelsi.model.OtpResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextView tvKembaliLogin;
    ImageButton btnBack;
    Button btnKirim;
    EditText etEmail;
    ApiInterface apiInterface;

    // Konstanta untuk Notifikasi
    private static final String CHANNEL_ID = "SimpelSi_OTP_Channel";
    private static final int NOTIFICATION_ID = 101;

    /**
     * Launcher untuk meminta izin notifikasi
     */
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Izin notifikasi diberikan.", Toast.LENGTH_SHORT).show();
                    // Coba panggil requestOtp lagi setelah izin diberikan
                    String email = etEmail.getText().toString().trim();
                    if (!email.isEmpty()) requestOtp(email);
                } else {
                    Toast.makeText(this, "Izin notifikasi ditolak. OTP tidak akan muncul di status bar.", Toast.LENGTH_LONG).show();
                    // Tetap panggil requestOtp agar alur lanjut (OTP bisa dilihat di Toast)
                    String email = etEmail.getText().toString().trim();
                    if (!email.isEmpty()) requestOtp(email);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupakatasandi);

        // Inisialisasi
        tvKembaliLogin = findViewById(R.id.tv_kembali_login);
        btnBack = findViewById(R.id.btn_back);
        btnKirim = findViewById(R.id.btn_kirim);
        etEmail = findViewById(R.id.et_email_lupa_sandi); // ❗️ Pastikan ID ini benar di XML
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        createNotificationChannel();

        // Listener
        tvKembaliLogin.setOnClickListener(v -> finish());
        btnBack.setOnClickListener(v -> finish());

        // Tombol Kirim memanggil fungsi cek izin dulu
        btnKirim.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(ForgotPasswordActivity.this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }
            askNotificationPermissionAndRequestOtp(email);
        });
    }

    /**
     * Method untuk cek & minta izin, lalu panggil requestOtp
     */
    private void askNotificationPermissionAndRequestOtp(String email) {
        // Hanya perlu izin untuk Android 13 (Tiramisu / API 33) ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // Izin sudah ada
                Log.d("NOTIFICATION_DEBUG", "Izin notifikasi sudah ada.");
                requestOtp(email);
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // Perlu penjelasan (jika pernah ditolak)
                Log.d("NOTIFICATION_DEBUG", "Perlu penjelasan izin notifikasi.");
                Toast.makeText(this, "Aplikasi membutuhkan izin untuk menampilkan notifikasi OTP.", Toast.LENGTH_LONG).show();
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                // Langsung minta izin
                Log.d("NOTIFICATION_DEBUG", "Meminta izin notifikasi...");
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            // Untuk Android 12 ke bawah
            Log.d("NOTIFICATION_DEBUG", "OS < Android 13, tidak perlu izin runtime.");
            requestOtp(email);
        }
    }

    private void requestOtp(String email) {
        Toast.makeText(ForgotPasswordActivity.this, "Mengirim permintaan...", Toast.LENGTH_SHORT).show();
        OtpRequest request = new OtpRequest(email);
        Call<OtpResponse> call = apiInterface.requestOtp(request);

        call.enqueue(new Callback<OtpResponse>() {
            @Override
            public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OtpResponse otpResponse = response.body();

                    if (otpResponse.isSuccess()) {
                        // SUKSES DARI API
                        Toast.makeText(ForgotPasswordActivity.this, otpResponse.getMessage(), Toast.LENGTH_SHORT).show(); // Pesan dari PHP

                        String otpCode = otpResponse.getOtp();
                        Log.d("OTP_DEBUG", "OTP diterima dari server: " + otpCode); // Log OTP

                        if (otpCode != null && !otpCode.isEmpty()) {
                            // Cek lagi izin sebelum menampilkan (untuk kasus izin diberikan saat runtime)
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                                    ContextCompat.checkSelfPermission(ForgotPasswordActivity.this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                                Log.d("NOTIFICATION_DEBUG", "Izin ada atau OS < 13, memanggil showOtpNotification...");
                                showOtpNotification(otpCode);
                            } else {
                                Log.w("NOTIFICATION_DEBUG", "Izin tidak diberikan, notifikasi tidak ditampilkan.");
                            }
                        } else {
                            Log.e("OTP_ERROR", "OTP null atau kosong diterima dari server!");
                        }

                        // Pindah ke VerificationActivity
                        Intent intent = new Intent(ForgotPasswordActivity.this, VerificationActivity.class);
                        intent.putExtra("USER_EMAIL", email);
                        startActivity(intent);

                    } else {
                        // GAGAL DARI API
                        Toast.makeText(ForgotPasswordActivity.this, otpResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    // GAGAL KONEKSI
                    Toast.makeText(ForgotPasswordActivity.this, "Gagal terhubung. Kode: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OtpResponse> call, Throwable t) {
                // GAGAL JARINGAN
                Toast.makeText(ForgotPasswordActivity.this, "Koneksi Gagal: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("REQUEST_OTP_FAILURE", "Error: " + t.getMessage(), t);
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "OTP Channel";
            String description = "Channel untuk menampilkan kode OTP";
            int importance = NotificationManager.IMPORTANCE_HIGH; // Pastikan HIGH
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                Log.d("NOTIFICATION_DEBUG", "Channel notifikasi dibuat atau sudah ada.");
            } else {
                Log.e("NOTIFICATION_ERROR", "Gagal mendapatkan NotificationManager saat membuat channel.");
            }
        }
    }

    /**
     * Method untuk menampilkan notifikasi OTP (dengan Logging Detail)
     */
    private void showOtpNotification(String otpCode) {
        Log.d("NOTIFICATION_DEBUG", "Memulai showOtpNotification dengan OTP: " + otpCode); // Log Awal

        // ❗️ Coba ganti ikon Bawaan Android untuk tes
        // int iconResId = android.R.drawable.ic_dialog_info;
        int iconResId = R.drawable.ic_notification; // ⚠️ Pastikan ikon ini benar

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(iconResId)
                .setContentTitle("Kode OTP SimpelSi Reset Password")
                .setContentText("Kode OTP Anda: " + otpCode)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Pastikan HIGH
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            // Cek channel lagi sebelum notify (untuk debug)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = notificationManager.getNotificationChannel(CHANNEL_ID);
                if (channel == null) {
                    Log.e("NOTIFICATION_ERROR", "Channel (" + CHANNEL_ID + ") TIDAK DITEMUKAN saat akan notify!");
                    // Jika channel hilang, notifikasi tidak akan muncul di Oreo+
                    // Anda bisa coba buat lagi di sini, tapi seharusnya tidak perlu
                    // createNotificationChannel();
                } else {
                    Log.d("NOTIFICATION_DEBUG", "Channel (" + CHANNEL_ID + ") ditemukan.");
                }
            }

            Log.d("NOTIFICATION_DEBUG", "Memanggil notificationManager.notify() dengan ID: " + NOTIFICATION_ID);
            try {
                notificationManager.notify(NOTIFICATION_ID, builder.build());
                Log.d("NOTIFICATION_DEBUG", "Pemanggilan notify() selesai. Notifikasi seharusnya muncul.");
            } catch (Exception e) {
                Log.e("NOTIFICATION_ERROR", "Error saat memanggil notify(): " + e.getMessage(), e);
            }
        } else {
            Log.e("NOTIFICATION_ERROR", "NotificationManager IS NULL! Tidak bisa menampilkan notifikasi.");
        }
    }
}