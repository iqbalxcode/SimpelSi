package id.polije.simpelsi.Tps;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText; // ❗️ Ubah TextView ke EditText
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast; // ❗️ Import Toast

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager; // ❗️ Import
import androidx.recyclerview.widget.RecyclerView; // ❗️ Import

import com.google.gson.Gson; // ❗️ Import

import java.util.ArrayList;
import java.util.List;

import id.polije.simpelsi.R;
import id.polije.simpelsi.api.ApiClient; // ❗️ Import
import id.polije.simpelsi.api.ApiInterface; // ❗️ Import
import id.polije.simpelsi.model.ResponseTps; // ❗️ Import
import retrofit2.Call; // ❗️ Import
import retrofit2.Callback; // ❗️ Import
import retrofit2.Response; // ❗️ Import

public class InfoTPSActivity extends AppCompatActivity {

    // ❗️ Ganti LinearLayout dengan RecyclerView
    private RecyclerView recyclerViewTps;
    private TpsAdapter adapter;
    private List<Tps> tpsList = new ArrayList<>();
    private ImageView btnBack;
    private EditText etSearch; // ❗️ Ganti tipe ke EditText
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_tps);

        // ❗️ Inisialisasi RecyclerView
        recyclerViewTps = findViewById(R.id.recyclerViewTps); // ❗️ Ganti ID di XML
        btnBack = findViewById(R.id.btnBack);
        etSearch = findViewById(R.id.etSearch); // ❗️ Pastikan tipe di XML adalah EditText

        // Inisialisasi API
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        // Setup RecyclerView
        recyclerViewTps.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TpsAdapter(this, tpsList);
        recyclerViewTps.setAdapter(adapter);

        // Tombol kembali
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        // ❗️ Panggil API untuk memuat data
        loadDataTps();

        // ❗️ Filter pencarian
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString()); // Panggil filter adapter
            }
        });
    }

    private void loadDataTps() {
        Call<ResponseTps> call = apiInterface.getAllTps();
        call.enqueue(new Callback<ResponseTps>() {
            @Override
            public void onResponse(Call<ResponseTps> call, Response<ResponseTps> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("InfoTPS", "Response JSON: " + new Gson().toJson(response.body()));

                    if ("success".equals(response.body().getStatus()) && response.body().getData() != null) {
                        adapter.updateData(response.body().getData());
                    } else {
                        Toast.makeText(InfoTPSActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(InfoTPSActivity.this, "Gagal mengambil data TPS", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseTps> call, Throwable t) {
                Toast.makeText(InfoTPSActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("InfoTPS", "Gagal load data: ", t);
            }
        });
    }
}