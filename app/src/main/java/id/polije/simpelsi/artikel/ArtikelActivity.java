package id.polije.simpelsi.artikel;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast; // ❗️ Import Toast

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager; // ❗️ Import
import androidx.recyclerview.widget.RecyclerView; // ❗️ Import

import java.util.ArrayList; // ❗️ Import
import java.util.List; // ❗️ Import

import id.polije.simpelsi.R;
import id.polije.simpelsi.api.ApiClient; // ❗️ Import
import id.polije.simpelsi.api.ApiInterface; // ❗️ Import
import id.polije.simpelsi.model.ResponseArtikel; // ❗️ Import
import retrofit2.Call; // ❗️ Import
import retrofit2.Callback; // ❗️ Import
import retrofit2.Response; // ❗️ Import

public class ArtikelActivity extends AppCompatActivity {

    // ❗️ Deklarasi komponen baru
    private RecyclerView recyclerView;
    private ArtikelAdapter adapter;
    private List<Artikel> artikelList = new ArrayList<>();
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artikel);

        // 🔹 Hubungkan tombol back
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> onBackPressed()); // Diringkas

        // ❗️ Inisialisasi RecyclerView
        recyclerView = findViewById(R.id.recyclerViewArtikel);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ArtikelAdapter(this, artikelList);
        recyclerView.setAdapter(adapter);

        // ❗️ Inisialisasi ApiInterface
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        // ❗️ Panggil method untuk memuat data
        loadArtikel();
    }

    /**
     * ❗️ Method BARU untuk memanggil API get_artikel.php
     */
    private void loadArtikel() {
        Call<ResponseArtikel> call = apiInterface.getArtikel();
        call.enqueue(new Callback<ResponseArtikel>() {
            @Override
            public void onResponse(Call<ResponseArtikel> call, Response<ResponseArtikel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equals(response.body().getStatus())) {
                        List<Artikel> data = response.body().getData();
                        if (data != null && !data.isEmpty()) {
                            adapter.updateData(data); // Update adapter
                        } else {
                            Toast.makeText(ArtikelActivity.this, "Tidak ada artikel ditemukan", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ArtikelActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ArtikelActivity.this, "Gagal mengambil data dari server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseArtikel> call, Throwable t) {
                Toast.makeText(ArtikelActivity.this, "Koneksi Gagal: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ArtikelActivity", "onFailure: ", t);
            }
        });
    }
}