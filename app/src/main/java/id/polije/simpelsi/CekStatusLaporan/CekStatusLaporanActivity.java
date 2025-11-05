package id.polije.simpelsi.CekStatusLaporan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import id.polije.simpelsi.R;
import id.polije.simpelsi.api.ApiClient;
import id.polije.simpelsi.api.ApiInterface;
import id.polije.simpelsi.CekStatusLaporan.Laporan;
import id.polije.simpelsi.model.ResponseLaporan;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CekStatusLaporanActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText etCariNama;
    private LaporanAdapter adapter;
    private List<Laporan> laporanList = new ArrayList<>(); // ❗️ List milik Activity

    private String idMasyarakat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cek_status);

        recyclerView = findViewById(R.id.recyclerViewStatus);
        etCariNama = findViewById(R.id.etCariNama);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LaporanAdapter(this, laporanList); // ❗️ Adapter menggunakan list ini
        recyclerView.setAdapter(adapter);

        // Ambil SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        idMasyarakat = prefs.getString("id_masyarakat", null);

        if (idMasyarakat == null || idMasyarakat.isEmpty()) {
            Toast.makeText(this, "Sesi Anda tidak ditemukan. Silakan login ulang.", Toast.LENGTH_LONG).show();
            return;
        }

        loadLaporan(); // Panggil setelah ID didapat

        etCariNama.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }
        });
    }

    private void loadLaporan() {
        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseLaporan> call = api.getLaporan(idMasyarakat);

        call.enqueue(new Callback<ResponseLaporan>() {
            @Override
            public void onResponse(Call<ResponseLaporan> call, Response<ResponseLaporan> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("CEK_LAPORAN", "Response JSON: " + new Gson().toJson(response.body()));

                    // --- ⬇️ INI LOGIKA YANG BENAR ⬇️ ---
                    if ("success".equals(response.body().getStatus()) && response.body().getData() != null) {

                        // 1. Bersihkan list milik Activity
                        laporanList.clear();
                        // 2. Tambahkan semua data baru ke list milik Activity
                        laporanList.addAll(response.body().getData());
                        // 3. Beri tahu adapter bahwa data sudah berubah
                        adapter.updateData(laporanList);

                        // 4. Cek JIKA list-nya KOSONG
                        if (laporanList.isEmpty()) {
                            Toast.makeText(CekStatusLaporanActivity.this, "Anda belum memiliki laporan", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        // 5. Jika status "error" dari PHP (misal: "Data laporan tidak ditemukan")
                        Toast.makeText(CekStatusLaporanActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    // --- ⬆️ AKHIR PERBAIKAN ⬆️ ---

                } else {
                    Toast.makeText(CekStatusLaporanActivity.this, "Response gagal dari server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseLaporan> call, Throwable t) {
                Toast.makeText(CekStatusLaporanActivity.this, "Gagal memuat data: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("CEK_LAPORAN_FAIL", "Error: ", t);
            }
        });
    }
}