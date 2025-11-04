package id.polije.simpelsi.Tps;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import id.polije.simpelsi.R;

public class InfoTPSActivity extends AppCompatActivity {

    private LinearLayout containerTPS;
    private ImageButton btnBack;
    private TextView etSearch;

    // 🔹 Data TPS disimpan dalam list agar bisa difilter
    private final List<String[]> allDataTPS = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_tps);

        containerTPS = findViewById(R.id.containerTPS);
        btnBack = findViewById(R.id.btnBack);
        etSearch = findViewById(R.id.etSearch);

        // Tombol kembali
        btnBack.setOnClickListener(v -> finish());

        // 🔹 Tambahkan data TPS (nanti bisa dari API)
        allDataTPS.add(new String[]{"TPS 1 Kecamatan Wilangan", "CV4W+434, Kauman, Kec. Nganjuk, Kabupaten Nganjuk, Jawa Timur 64411", "Wilangan", "tps1", "Tempat Pembuangan Sampah (TPS) 1 berada di Kecamatan Wilangan, Kelurahan Sukoharjo tepatnya di Jl. Wilangan"});
        allDataTPS.add(new String[]{"TPS 2 Kecamatan Bagor", "Jl. Bagor", "Bagor", "tps2", "TPS 2 berada di Kecamatan Bagor, tepatnya di Jl. Bagor"});
        allDataTPS.add(new String[]{"TPS 3 Kecamatan Berbek", "Jl. Berbek", "Berbek", "tps3", "TPS 3 berada di Kecamatan Berbek, tepatnya di Jl. Berbek"});
        allDataTPS.add(new String[]{"TPS 4 Kecamatan Kertosono", "Jl. Kertosono", "Kertosono", "tps4", "TPS 4 berada di Kecamatan Kertosono"});
        allDataTPS.add(new String[]{"TPS 5 Kecamatan Wilangan", "Jl. Wilangan", "Wilangan", "tps5", "TPS 5 berada di Kecamatan Wilangan"});
        allDataTPS.add(new String[]{"TPS 6 Kecamatan Gondang", "Jl. Gondang", "Gondang", "tps6", "TPS 6 berada di Kecamatan Gondang"});

        // 🔹 Tampilkan seluruh TPS pertama kali
        tampilkanData(allDataTPS);

        // 🔹 Filter pencarian
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData(s.toString());
            }
        });
    }

    private void tampilkanData(List<String[]> dataList) {
        containerTPS.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (String[] tps : dataList) {
            View itemView = inflater.inflate(R.layout.item_tps, containerTPS, false);

            TextView tvNama = itemView.findViewById(R.id.tvNamaTPS);
            TextView tvLokasi = itemView.findViewById(R.id.tvLokasiTPS);

            tvNama.setText(tps[0]);
            tvLokasi.setText(tps[1]);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(InfoTPSActivity.this, DetailTPSActivity.class);
                intent.putExtra("nama", tps[0]);
                intent.putExtra("kecamatan", tps[2]);
                intent.putExtra("lokasi", tps[1]);
                intent.putExtra("gambar", tps[3]);
                intent.putExtra("keterangan", tps[4]);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });

            containerTPS.addView(itemView);
        }
    }

    private void filterData(String query) {
        List<String[]> filteredList = new ArrayList<>();
        for (String[] tps : allDataTPS) {
            if (tps[0].toLowerCase().contains(query.toLowerCase()) ||
                    tps[1].toLowerCase().contains(query.toLowerCase()) ||
                    tps[2].toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(tps);
            }
        }
        tampilkanData(filteredList);
    }
}
