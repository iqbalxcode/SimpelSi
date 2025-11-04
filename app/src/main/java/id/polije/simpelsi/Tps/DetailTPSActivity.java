package id.polije.simpelsi.Tps;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import id.polije.simpelsi.R;

public class DetailTPSActivity extends AppCompatActivity {

    private ImageView imgTPS;
    private TextView tvNamaTPS, tvKecamatan, tvLokasi, tvKeterangan;
    private TextView btnMaps;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tps);

        // 🔹 Inisialisasi komponen tampilan
        imgTPS = findViewById(R.id.imgTPS);
        tvNamaTPS = findViewById(R.id.tvNamaTPS);
        tvKecamatan = findViewById(R.id.tvKecamatan);
        tvLokasi = findViewById(R.id.tvLokasi);
        tvKeterangan = findViewById(R.id.tvKeterangan);
        btnMaps = findViewById(R.id.btnMaps);
        btnBack = findViewById(R.id.btnBack);

        // 🔹 Ambil data dari Intent dengan pengecekan aman
        Intent intent = getIntent();
        String nama = intent.getStringExtra("nama");
        String kecamatan = intent.getStringExtra("kecamatan");
        String lokasi = intent.getStringExtra("lokasi");
        String gambar = intent.getStringExtra("gambar");
        String keterangan = intent.getStringExtra("keterangan");

        // 🔹 Tampilkan data di layout dengan nilai default jika null
        tvNamaTPS.setText(nama != null ? nama : "Nama TPS tidak tersedia");
        tvKecamatan.setText("Kecamatan : " + (kecamatan != null ? kecamatan : "-"));
        tvLokasi.setText("Lokasi : " + (lokasi != null ? lokasi : "-"));
        tvKeterangan.setText(keterangan != null ? keterangan : "Tidak ada keterangan.");

        // 🔹 Coba pasang gambar dari drawable (aman dari error)
        try {
            if (gambar != null && !gambar.isEmpty()) {
                int resId = getResources().getIdentifier(gambar, "drawable", getPackageName());
                if (resId != 0) {
                    imgTPS.setImageResource(resId);
                } else {
                    imgTPS.setImageResource(R.drawable.tps); // gambar default
                }
            } else {
                imgTPS.setImageResource(R.drawable.tps);
            }
        } catch (Exception e) {
            imgTPS.setImageResource(R.drawable.tps);
        }

        // 🔹 Tombol buka Google Maps
        btnMaps.setOnClickListener(v -> {
            if (lokasi != null && !lokasi.isEmpty()) {
                try {
                    String mapQuery = "geo:0,0?q=" + Uri.encode(lokasi);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapQuery));

                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    } else {
                        // Buka lewat browser jika aplikasi peta tidak tersedia
                        String url = "https://www.google.com/maps/search/?api=1&query=" + Uri.encode(lokasi);
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Gagal membuka peta", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Lokasi tidak tersedia", Toast.LENGTH_SHORT).show();
            }
        });


        // 🔹 Tombol kembali
        btnBack.setOnClickListener(v -> finish());
    }
}
