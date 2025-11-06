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
import id.polije.simpelsi.model.ResponseLaporan;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CekStatusLaporanActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText etCariNama;
    private LaporanAdapter adapter;
    private final List<Laporan> laporanList = new ArrayList<>();
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

        // Ambil ID user dari SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        idMasyarakat = prefs.getString("id_masyarakat", null);

        if (idMasyarakat == null || idMasyarakat.isEmpty()) {
            Toast.makeText(this, "Sesi Anda tidak ditemukan. Silakan login ulang.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Muat data laporan dari API
        loadLaporan();

        // Fitur pencarian
        etCariNama.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null && !laporanList.isEmpty()) {
                    adapter.filter(s.toString().trim());
                }
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
                    ResponseLaporan body = response.body();
                    Log.d("CEK_LAPORAN", "Response JSON: " + new Gson().toJson(body));

                    if ("success".equalsIgnoreCase(body.getStatus())) {
                        List<Laporan> data = body.getData();

                        if (data != null && !data.isEmpty()) {
                            laporanList.clear();
                            laporanList.addAll(data);
                            adapter.updateData(data);
                            Log.d("CEK_LAPORAN", "Jumlah data laporan: " + data.size());
                        } else {
                            laporanList.clear();
                            adapter.updateData(laporanList);
                            Toast.makeText(CekStatusLaporanActivity.this,
                                    "Anda belum memiliki laporan.", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(CekStatusLaporanActivity.this,
                                body.getMessage() != null ? body.getMessage() : "Data laporan tidak ditemukan.",
                                Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(CekStatusLaporanActivity.this,
                            "Gagal memproses response dari server.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseLaporan> call, Throwable t) {
                Log.e("CEK_LAPORAN_FAIL", "Error saat memuat data", t);
                Toast.makeText(CekStatusLaporanActivity.this,
                        "Tidak dapat terhubung ke server: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
