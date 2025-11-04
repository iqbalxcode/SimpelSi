package id.polije.simpelsi.CekStatusLaporan;

import android.content.SharedPreferences; // ❗️ Import SharedPreferences
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
// ❗️ Pastikan import 'Laporan' ini benar
import id.polije.simpelsi.CekStatusLaporan.Laporan;
// ❗️ Pastikan import 'ResponseLaporan' ini benar
import id.polije.simpelsi.model.ResponseLaporan;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CekStatusLaporanActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText etCariNama;
    private LaporanAdapter adapter;
    private List<Laporan> laporanList = new ArrayList<>();

    // ❗️ Variabel ini akan kita isi dari SharedPreferences
    private String idMasyarakat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cek_status);

        recyclerView = findViewById(R.id.recyclerViewStatus);
        etCariNama = findViewById(R.id.etCariNama);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LaporanAdapter(this, laporanList);
        recyclerView.setAdapter(adapter);

        // --- ⬇️ PERBAIKAN UTAMA DI SINI ⬇️ ---

        // 1. Ambil SharedPreferences (nama harus sama persis dengan di LoginActivity)
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);

        // 2. Ambil "id_masyarakat" yang tersimpan
        idMasyarakat = prefs.getString("id_masyarakat", null);

        // 3. Cek jika ID ada
        if (idMasyarakat == null || idMasyarakat.isEmpty()) {
            Toast.makeText(this, "Sesi Anda tidak ditemukan. Silakan login ulang.", Toast.LENGTH_LONG).show();
            // (Opsional: Anda bisa paksa kembali ke LoginActivity di sini jika mau)
            // finish();
            return; // Hentikan jika tidak ada ID
        }

        // 4. Panggil loadLaporan SETELAH ID didapatkan
        loadLaporan();

        // --- ⬆️ AKHIR PERBAIKAN ⬆️ ---

        // 🔍 fitur pencarian berdasarkan nama (Ini sudah benar)
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

        // ❗️ Panggilan ini sekarang akan berisi ID pengguna (misal: "14"), bukan null
        Call<ResponseLaporan> call = api.getLaporan(idMasyarakat);

        call.enqueue(new Callback<ResponseLaporan>() {
            @Override
            public void onResponse(Call<ResponseLaporan> call, Response<ResponseLaporan> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("CEK_LAPORAN", "Response JSON: " + new Gson().toJson(response.body()));

                    // ❗️ Perbaikan logika untuk menangani status "success" atau "error"
                    if ("success".equals(response.body().getStatus()) && response.body().getData() != null) {

                        laporanList.clear(); // Bersihkan list sebelum diisi
                        laporanList.addAll(response.body().getData());
                        adapter.updateData(laporanList); // Update adapter dengan data baru

                        if (laporanList.isEmpty()) {
                            Toast.makeText(CekStatusLaporanActivity.this, "Anda belum memiliki laporan", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        // Ini terjadi jika status "error" dari PHP (misal: "Data laporan tidak ditemukan")
                        Toast.makeText(CekStatusLaporanActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CekStatusLaporanActivity.this, "Response gagal dari server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseLaporan> call, Throwable t) {
                Toast.makeText(CekStatusLaporanActivity.this, "Gagal memuat data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("CEK_LAPORAN_FAIL", "Error: ", t);
            }
        });
    }

}