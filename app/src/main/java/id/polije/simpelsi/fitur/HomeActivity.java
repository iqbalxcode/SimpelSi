package id.polije.simpelsi.fitur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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
    private LinearLayout headerDokumentasi; // Header "Trending Artikel"
    private BottomNavigationView bottomNavigationView;

    // --- ⬇️ VARIABEL TRENDING ARTIKEL ⬇️ ---
    private RecyclerView recyclerViewTrending;
    private ArtikelTrendingAdapter trendingAdapter;
    private List<Artikel> trendingArtikelList = new ArrayList<>();
    private ApiInterface apiInterface;
    // --- ⬆️ AKHIR VARIABEL ⬆️ ---


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        setupClickListeners();
        setupBottomNav();

        // --- ⬇️ SETUP TRENDING ⬇️ ---
        setupTrendingRecyclerView();
        loadTrendingArtikel();
        // --- ⬆️ AKHIR SETUP ⬆️ ---
    }

    /**
     * Inisialisasi semua view dari XML
     */
    private void initViews() {
        layananCekStatus = findViewById(R.id.layananCekStatus);
        layananArtikel = findViewById(R.id.layananArtikel);
        layananInfoTPS = findViewById(R.id.layananInfoTPS);
        headerDokumentasi = findViewById(R.id.headerTrendingArtikel);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // ❗️ Inisialisasi komponen baru
        recyclerViewTrending = findViewById(R.id.recyclerViewTrending);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
    }

    /**
     * Listener untuk klik fitur layanan & header artikel
     */
    private void setupClickListeners() {
        layananCekStatus.setOnClickListener(v -> {
            startActivity(new Intent(this, CekStatusLaporanActivity.class));
        });

        layananArtikel.setOnClickListener(v -> {
            startActivity(new Intent(this, ArtikelActivity.class));
        });

        layananInfoTPS.setOnClickListener(v -> {
            startActivity(new Intent(this, InfoTPSActivity.class));
        });

        // Klik header Trending Artikel mengarah ke halaman Artikel
        headerDokumentasi.setOnClickListener(v -> {
            startActivity(new Intent(this, ArtikelActivity.class));
        });
    }

    /**
     * Setup Bottom Navigation
     */
    private void setupBottomNav() {
        if (bottomNavigationView == null) return;

        // Tandai halaman ini sebagai aktif
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_pengajuan) {
                startActivity(new Intent(HomeActivity.this, PengajuanLaporanActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_profil) {
                startActivity(new Intent(HomeActivity.this, ProfilActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }

            return false;
        });
    }

    // --- ⬇️ METHOD BARU UNTUK RECYCLERVIEW ⬇️ ---

    /**
     * Setup RecyclerView Trending (Horizontal)
     */
    private void setupTrendingRecyclerView() {
        // Atur agar RecyclerView bisa digeser horizontal
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTrending.setLayoutManager(layoutManager);

        // Buat adapter baru
        trendingAdapter = new ArtikelTrendingAdapter(this, trendingArtikelList);
        recyclerViewTrending.setAdapter(trendingAdapter);
    }

    /**
     * Memuat data artikel dari API
     */
    private void loadTrendingArtikel() {
        Call<ResponseArtikel> call = apiInterface.getArtikel();
        call.enqueue(new Callback<ResponseArtikel>() {
            @Override
            public void onResponse(Call<ResponseArtikel> call, Response<ResponseArtikel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equals(response.body().getStatus())) {
                        List<Artikel> data = response.body().getData();
                        if (data != null && !data.isEmpty()) {
                            // Ambil data, dan hanya tampilkan 4 item teratas (opsional)
                            List<Artikel> limitedData = (data.size() > 4) ? data.subList(0, 4) : data;
                            trendingAdapter.updateData(limitedData);
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
    // --- ⬆️ AKHIR METHOD BARU ⬆️ ---
}