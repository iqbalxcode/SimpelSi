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
// ❗️ Asumsi DetailResponse dan DataLaporan sudah Anda buat
import id.polije.simpelsi.CekStatusLaporan.DetailResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailTanggapanActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView ivFotoLaporan;
    private TextView tvNama, tvLokasi, tvKeterangan, tvTanggal, tvStatus, tvTanggapan;
    private ApiInterface apiInterface;
    private String idLaporan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tanggapan);

        // 🔹 Inisialisasi view (Sudah benar)
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

    private void loadDetailLaporan(String idLaporan) {
        Toast.makeText(this, "Memuat detail laporan...", Toast.LENGTH_SHORT).show();

        // ❗️ Panggilan API yang benar
        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);
        Call<DetailResponse> call = api.getDetailLaporan(idLaporan);

        call.enqueue(new Callback<DetailResponse>() {
            @Override
            public void onResponse(Call<DetailResponse> call, Response<DetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DetailResponse body = response.body();

                    if (body.isSuccess() && body.getData() != null) {
                        DetailResponse.DataLaporan detail = body.getData();

                        // 1. Tampilkan Data Teks
                        tvNama.setText("Nama : " + detail.getNama());
                        tvLokasi.setText("Lokasi : " + detail.getLokasi());
                        tvKeterangan.setText("Keterangan : " + detail.getKeterangan());
                        tvTanggal.setText("Tanggal : " + detail.getTanggal());
                        tvStatus.setText(detail.getStatus());
                        tvTanggapan.setText(
                                detail.getBalasan() != null && !detail.getBalasan().isEmpty()
                                        ? detail.getBalasan()
                                        : "Belum ada tanggapan."
                        );

                        // 2. Atur warna status (Logika sudah benar)
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

                        // 3. Tampilkan foto laporan (Gunakan Proxy)
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
                        // Respon success, tapi status error (misal: "Laporan tidak ditemukan")
                        Toast.makeText(DetailTanggapanActivity.this,
                                body.getMessage() != null ? body.getMessage() : "Data laporan tidak ditemukan.",
                                Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // Gagal koneksi (kode 404/500)
                    Toast.makeText(DetailTanggapanActivity.this,
                            "Gagal memproses response dari server.", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<DetailResponse> call, Throwable t) {
                // Gagal jaringan atau timeout
                Toast.makeText(DetailTanggapanActivity.this,
                        "Koneksi gagal/Timeout: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e("DetailTanggapan", "Koneksi Gagal: ", t);
            }
        });
    }
}