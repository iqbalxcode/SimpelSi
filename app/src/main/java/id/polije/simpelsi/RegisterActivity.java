package id.polije.simpelsi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class RegisterActivity extends AppCompatActivity {

    Button btnKirim;
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inisialisasi komponen
        btnKirim = findViewById(R.id.btn_kirim_register);
        btnBack = findViewById(R.id.btn_back);

        // Tombol kirim (belum ada logika, nanti bisa kamu tambahkan validasi di sini)
        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tambahkan logika kirim data ke server di sini
            }
        });

        // Tombol back ke halaman login
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // opsional: agar tidak bisa kembali ke halaman register
            }
        });
    }
}
