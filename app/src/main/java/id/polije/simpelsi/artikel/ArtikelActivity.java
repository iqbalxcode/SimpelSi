package id.polije.simpelsi.artikel;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import id.polije.simpelsi.R;

public class ArtikelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artikel); // pastikan layout-nya sesuai

        // 🔹 Hubungkan tombol back dengan ID dari layout XML
        ImageView btnBack = findViewById(R.id.btn_back);

        // 🔹 Tambahkan aksi klik
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tutup halaman saat tombol back ditekan
                ArtikelActivity.super.onBackPressed(); // ✅ ini yang benar
            }
        });
    }
}
