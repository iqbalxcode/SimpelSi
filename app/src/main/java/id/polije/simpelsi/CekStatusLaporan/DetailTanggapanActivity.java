package id.polije.simpelsi.CekStatusLaporan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;

import id.polije.simpelsi.R;
import id.polije.simpelsi.api.ApiClient;
import id.polije.simpelsi.api.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailTanggapanActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView ivFotoLaporan;
    private TextView tvNama, tvLokasi, tvKeterangan, tvTanggal, tvStatus, tvTanggapan;

    private ApiInterface apiInterface;
    private String idLaporan; // 🔹 ID dikirim dari intent sebagai String

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tanggapan);

        // 🔹 Inisialisasi view
        btnBack = findViewById(R.id.btnBack);
        ivFotoLaporan = findViewById(R.id.ivFotoLaporan);
        tvNama = findViewById(R.id.tvNama);
        tvLokasi = findViewById(R.id.tvLokasi);
        tvKeterangan = findViewById(R.id.tvKeterangan);
        tvTanggal = findViewById(R.id.tvTanggal);
        tvStatus = findViewById(R.id.tvStatus);
        tvTanggapan = findViewById(R.id.tvTanggapan);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        // 🔹 Ambil id_laporan dari Intent
        Intent intent = getIntent();
        if (intent != null) {
            idLaporan = intent.getStringExtra("id_laporan");

            if (idLaporan != null && !idLaporan.isEmpty()) {
                loadDetailLaporan(idLaporan);
            } else {
                Toast.makeText(this, "ID laporan tidak valid", Toast.LENGTH_SHORT).show();
            }
        }

        btnBack.setOnClickListener(v -> finish());
    }

    // 🔹 Ubah parameter jadi String agar sesuai dengan API
    private void loadDetailLaporan(String idLaporan) {
        Call<DetailResponse> call = apiInterface.getDetailLaporan(idLaporan);
        call.enqueue(new Callback<DetailResponse>() {
            @Override
            public void onResponse(Call<DetailResponse> call, Response<DetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    DetailResponse.DataLaporan detail = response.body().getData();

                    tvNama.setText("Nama : " + detail.getNama());
                    tvLokasi.setText("Lokasi : " + detail.getLokasi());
                    tvKeterangan.setText("Keterangan : " + detail.getKeterangan());
                    tvTanggal.setText("Tanggal : " + detail.getTanggal());
                    tvStatus.setText(detail.getStatus());
                    tvTanggapan.setText(
                            detail.getBalasan() != null ? detail.getBalasan() : "Belum ada tanggapan."
                    );

                    // 🔹 Atur warna status
                    if (detail.getStatus() != null) {
                        switch (detail.getStatus().toLowerCase()) {
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
                    }

                    // 🔹 Tampilkan foto laporan dari get_image.php (sama seperti di LaporanAdapter)
                    Glide.with(DetailTanggapanActivity.this)
                            .load(ApiClient.BASE_URL + "get_image.php?file=" + detail.getFoto())
                            .placeholder(R.drawable.loading)
                            .error(R.drawable.loading)
                            .into(ivFotoLaporan);

                } else {
                    Toast.makeText(DetailTanggapanActivity.this,
                            "Gagal memuat data laporan: " + response.message(),
                            Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<DetailResponse> call, Throwable t) {
                Toast.makeText(DetailTanggapanActivity.this,
                        "Koneksi gagal: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
