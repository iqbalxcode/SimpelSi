package id.polije.simpelsi.artikel;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import id.polije.simpelsi.R;
import id.polije.simpelsi.api.ApiClient;

public class ArtikelDetailActivity extends AppCompatActivity {

    private ImageView imgDetailArtikel;
    private TextView tvDetailJudul, tvDetailTanggal, tvDetailDeskripsi;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artikel_detail);

        // Inisialisasi Views
        imgDetailArtikel = findViewById(R.id.imgDetailArtikel);
        tvDetailJudul = findViewById(R.id.tvDetailJudul);
        tvDetailTanggal = findViewById(R.id.tvDetailTanggal);
        tvDetailDeskripsi = findViewById(R.id.tvDetailDeskripsi);
        toolbar = findViewById(R.id.toolbar);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
// Lanjutkan di dalam onCreate() atau setelah layout di-inflate:
        ImageView btnBack = findViewById(R.id.btnBack);

// Terapkan fungsi 'finish()' ke ImageView kustom Anda
        btnBack.setOnClickListener(v -> {
            finish(); // Ini adalah fungsi yang sama dengan aksi tombol back bawaan
        });

        // Setup Toolbar (tombol back)
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
           // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish()); // Aksi tombol back

        // Ambil data Artikel dari Intent
        Artikel artikel = (Artikel) getIntent().getSerializableExtra("ARTIKEL_DATA");

        if (artikel == null) {
            Toast.makeText(this, "Gagal memuat artikel", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Isi data ke tampilan
        collapsingToolbar.setTitle(artikel.getJudul()); // Judul di toolbar saat collapse
        tvDetailJudul.setText(artikel.getJudul());
        tvDetailTanggal.setText(artikel.getTanggal());
        tvDetailDeskripsi.setText(artikel.getDeskripsi());

        // Muat gambar menggunakan Glide dan Proxy (get_image.php)
        String namaFileFoto = artikel.getFoto();
        if (namaFileFoto != null && !namaFileFoto.trim().isEmpty()) {
            String urlProxy = ApiClient.BASE_URL + "get_image.php?file=" + namaFileFoto + "&tipe=artikel";

            Glide.with(this)
                    .load(urlProxy)
                    .centerCrop()
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.loading)
                    .into(imgDetailArtikel);
        } else {
            imgDetailArtikel.setImageResource(R.drawable.loading);
        }
    }
}