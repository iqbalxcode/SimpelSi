package id.polije.simpelsi.artikel;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import id.polije.simpelsi.R;
import id.polije.simpelsi.api.ApiClient;

public class ArtikelDetailActivity extends AppCompatActivity {

    private ImageView imgDetailArtikel;
    private TextView tvDetailJudul, tvDetailTanggal, tvDetailDeskripsi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artikel_detail);

        // Ambil View dari layout baru
        imgDetailArtikel = findViewById(R.id.imgDetailArtikel);
        tvDetailJudul = findViewById(R.id.tvDetailJudul);
        tvDetailTanggal = findViewById(R.id.tvDetailTanggal);
        tvDetailDeskripsi = findViewById(R.id.tvDetailDeskripsi);

        // Tombol Back Custom
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Ambil data Artikel dari Intent
        Artikel artikel = (Artikel) getIntent().getSerializableExtra("ARTIKEL_DATA");

        if (artikel == null) {
            Toast.makeText(this, "Gagal memuat artikel", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set data ke tampilan
        tvDetailJudul.setText(artikel.getJudul());
        tvDetailTanggal.setText(artikel.getTanggal());
        tvDetailDeskripsi.setText(artikel.getDeskripsi());

        // Load gambar artikel
        String namaFileFoto = artikel.getFoto();
        if (namaFileFoto != null && !namaFileFoto.trim().isEmpty()) {

            String urlProxy = ApiClient.BASE_URL +
                    "get_image.php?file=" + namaFileFoto + "&tipe=artikel";

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
