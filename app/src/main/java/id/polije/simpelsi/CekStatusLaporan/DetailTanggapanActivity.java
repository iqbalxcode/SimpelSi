package id.polije.simpelsi.CekStatusLaporan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;

import id.polije.simpelsi.R;
import id.polije.simpelsi.api.ApiClient;
import id.polije.simpelsi.api.ApiInterface;
import id.polije.simpelsi.CekStatusLaporan.DetailResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 🆕 import tambahan untuk text styling
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.graphics.Typeface;

public class DetailTanggapanActivity extends AppCompatActivity {

    private ImageView btnBack;
    private ImageView ivFotoLaporan;
    private TextView tvNama, tvLokasi, tvKeterangan, tvTanggal, tvStatus, tvTanggapan;
    private ApiInterface apiInterface;
    private String idLaporan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tanggapan);

        btnBack = findViewById(R.id.btnBack);
        ivFotoLaporan = findViewById(R.id.ivFotoLaporan);
        tvNama = findViewById(R.id.tvNama);
        tvLokasi = findViewById(R.id.tvLokasi);
        tvKeterangan = findViewById(R.id.tvKeterangan);
        tvTanggal = findViewById(R.id.tvTanggal);
        tvStatus = findViewById(R.id.tvStatus);
        tvTanggapan = findViewById(R.id.tvTanggapan);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Intent intent = getIntent();
        if (intent != null) {
            idLaporan = intent.getStringExtra("id_laporan");

            if (idLaporan != null && !idLaporan.isEmpty()) {
                loadDetailLaporan(idLaporan);
            } else {
                Toast.makeText(this, "ID laporan tidak valid", Toast.LENGTH_SHORT).show();
            }
        }

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void loadDetailLaporan(String idLaporan) {
        Toast.makeText(this, "Memuat detail laporan...", Toast.LENGTH_SHORT).show();

        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);
        Call<DetailResponse> call = api.getDetailLaporan(idLaporan);

        call.enqueue(new Callback<DetailResponse>() {
            @Override
            public void onResponse(Call<DetailResponse> call, Response<DetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DetailResponse body = response.body();

                    if (body.isSuccess() && body.getData() != null) {
                        DetailResponse.DataLaporan detail = body.getData();

                        // 🆕 Gunakan fungsi boldLabel() agar label tebal, isi normal
                        tvNama.setText(boldLabel("Nama : ", detail.getNama()));
                        tvLokasi.setText(boldLabel("Lokasi : ", detail.getLokasi()));
                        tvKeterangan.setText(boldLabel("Keterangan : ", detail.getKeterangan()));
                        tvTanggal.setText(boldLabel("Tanggal : ", detail.getTanggal()));

                        tvStatus.setText(detail.getStatus());
                        tvTanggapan.setText(
                                detail.getBalasan() != null && !detail.getBalasan().isEmpty()
                                        ? detail.getBalasan()
                                        : "Belum ada tanggapan."
                        );

                        // 2. Atur warna status
                        String status = detail.getStatus() != null ? detail.getStatus().toLowerCase() : "";
                        switch (status) {
                            case "diterima":
                                tvStatus.setBackgroundResource(R.drawable.bg_status_diterima);
                                break;
                            case "ditolak":
                                tvStatus.setBackgroundResource(R.drawable.bg_status_ditolak);
                                break;
                            default:
                                tvStatus.setBackgroundResource(R.drawable.bg_status_diproses);
                                break;
                        }

                        // 3. Tampilkan foto laporan
                        String namaFoto = detail.getFoto();
                        if (namaFoto != null && !namaFoto.isEmpty()) {
                            String urlProxy = ApiClient.BASE_URL + "get_image.php?file=" + namaFoto;
                            Log.d("DetailTanggapan", "Memuat foto: " + urlProxy);

                            Glide.with(DetailTanggapanActivity.this)
                                    .load(urlProxy)
                                    .placeholder(R.drawable.loading)
                                    .error(R.drawable.loading)
                                    .into(ivFotoLaporan);
                        } else {
                            ivFotoLaporan.setImageResource(R.drawable.loading);
                        }

                    } else {
                        Toast.makeText(DetailTanggapanActivity.this,
                                body.getMessage() != null ? body.getMessage() : "Data laporan tidak ditemukan.",
                                Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(DetailTanggapanActivity.this,
                            "Gagal memproses response dari server.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DetailResponse> call, Throwable t) {
                Toast.makeText(DetailTanggapanActivity.this,
                        "Koneksi gagal/Timeout: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e("DetailTanggapan", "Koneksi Gagal: ", t);
            }
        });
    }

    // 🆕 Tambahan fungsi untuk membuat label bold otomatis
    private SpannableString boldLabel(String label, String value) {
        SpannableString s = new SpannableString(label + (value != null ? value : ""));
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
    }
}
