package id.polije.simpelsi.fitur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem; // ❗️ Pastikan ini di-import
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView; // ❗️ Pastikan ini di-import

import java.util.ArrayList;
import java.util.List;

import id.polije.simpelsi.CekStatusLaporan.CekStatusLaporanActivity;
import id.polije.simpelsi.R;
import id.polije.simpelsi.Tps.InfoTPSActivity;
import id.polije.simpelsi.api.ApiClient;
import id.polije.simpelsi.api.ApiInterface;
import id.polije.simpelsi.artikel.Artikel;
import id.polije.simpelsi.artikel.ArtikelActivity;
import id.polije.simpelsi.artikel.ArtikelTrendingAdapter;
import id.polije.simpelsi.model.ResponseArtikel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout layananCekStatus, layananArtikel, layananInfoTPS;
    private LinearLayout headerDokumentasi;
    private BottomNavigationView bottomNavigationView;

    private RecyclerView recyclerViewTrending;
    private ArtikelTrendingAdapter trendingAdapter;
    private List<Artikel> trendingArtikelList = new ArrayList<>();
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        setupClickListeners();
        setupBottomNav(); // ❗️ Panggil method ini

        setupTrendingRecyclerView();
        loadTrendingArtikel();
    }

    private void initViews() {
        layananCekStatus = findViewById(R.id.layananCekStatus);
        layananArtikel = findViewById(R.id.layananArtikel);
        layananInfoTPS = findViewById(R.id.layananInfoTPS);
        headerDokumentasi = findViewById(R.id.headerTrendingArtikel);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        recyclerViewTrending = findViewById(R.id.recyclerViewTrending);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
    }

    private void setupClickListeners() {
        // ... (Kode Anda sudah benar) ...
        layananCekStatus.setOnClickListener(v -> {
            startActivity(new Intent(this, CekStatusLaporanActivity.class));
        });
        layananArtikel.setOnClickListener(v -> {
            startActivity(new Intent(this, ArtikelActivity.class));
        });
        layananInfoTPS.setOnClickListener(v -> {
            startActivity(new Intent(this, InfoTPSActivity.class));
        });
        headerDokumentasi.setOnClickListener(v -> {
            startActivity(new Intent(this, ArtikelActivity.class));
        });
    }

    /**
     * Setup Bottom Navigation
     */
    // --- ⬇️ PERBAIKAN DI SINI: ISI METHOD INI ⬇️ ---
    private void setupBottomNav() {
        if (bottomNavigationView == null) return;

        // Tandai halaman ini sebagai aktif
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                // Sudah di halaman ini
                return true;
            } else if (id == R.id.nav_pengajuan) {
                // Pindah ke Pengajuan Laporan
                startActivity(new Intent(HomeActivity.this, PengajuanLaporanActivity.class));
                overridePendingTransition(0, 0); // Transisi instan
                finish(); // Tutup HomeActivity
                return true;
            } else if (id == R.id.nav_profil) {
                // Pindah ke Profil
                startActivity(new Intent(HomeActivity.this, ProfilActivity.class));
                overridePendingTransition(0, 0); // Transisi instan
                finish(); // Tutup HomeActivity
                return true;
            }

            return false;
        });
    }
    // --- ⬆️ AKHIR PERBAIKAN ⬆️ ---

    /**
     * Setup RecyclerView Trending (Horizontal)
     */
    private void setupTrendingRecyclerView() {
        // ... (Kode Anda sudah benar) ...
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTrending.setLayoutManager(layoutManager);
        trendingAdapter = new ArtikelTrendingAdapter(this, trendingArtikelList);
        recyclerViewTrending.setAdapter(trendingAdapter);
    }

    /**
     * Memuat data artikel dari API
     */
    private void loadTrendingArtikel() {
        // ... (Kode Anda sudah benar) ...
        Call<ResponseArtikel> call = apiInterface.getArtikel();
        call.enqueue(new Callback<ResponseArtikel>() {
            @Override
            public void onResponse(Call<ResponseArtikel> call, Response<ResponseArtikel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equals(response.body().getStatus())) {
                        List<Artikel> data = response.body().getData();
                        if (data != null && !data.isEmpty()) {
                            trendingAdapter.updateData(data); // Update adapter
                        } else {
                            Log.w("HomeActivity", "Tidak ada artikel trending ditemukan");
                        }
                    } else {
                        Log.w("HomeActivity", "Status API bukan success: " + response.body().getMessage());
                    }
                } else {
                    Log.w("HomeActivity", "Gagal mengambil data trending");
                }
            }
            @Override
            public void onFailure(Call<ResponseArtikel> call, Throwable t) {
                Log.e("HomeActivity", "onFailure loadTrending: ", t);
            }
        });
    }
}