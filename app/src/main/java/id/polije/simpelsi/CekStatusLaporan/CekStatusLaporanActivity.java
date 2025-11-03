package id.polije.simpelsi.CekStatusLaporan;

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
    private List<Laporan> laporanList = new ArrayList<>();

    // 🧩 ganti sesuai dengan ID masyarakat yang login
    private String idMasyarakat; // contoh ID masyarakat dari database

    // contoh sementara, nanti diambil dari session/login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cek_status);

        recyclerView = findViewById(R.id.recyclerViewStatus);
        etCariNama = findViewById(R.id.etCariNama);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LaporanAdapter(this, laporanList);
        recyclerView.setAdapter(adapter);

        loadLaporan();

        // 🔍 fitur pencarian berdasarkan nama
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

                    if (response.body().getData() != null) {
                        adapter.updateData(response.body().getData());
                    }
                    if (laporanList.isEmpty()) {
                        Toast.makeText(CekStatusLaporanActivity.this, "Tidak ada laporan ditemukan", Toast.LENGTH_SHORT).show();
                    }

                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(CekStatusLaporanActivity.this, "Response gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseLaporan> call, Throwable t) {
                Toast.makeText(CekStatusLaporanActivity.this, "Gagal memuat data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}