package id.polije.simpelsi.Tps;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import id.polije.simpelsi.R;

public class DetailTPSActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private LatLng tpsLatLng; // Variabel untuk menyimpan koordinat
    private String tpsNama;

    // Variabel UI (tvKecamatan dihapus)
    private TextView tvNamaTPS, tvAlamat, tvLokasi, tvKeterangan, tvKapasitas, tvJudul;
    private TextView btnMaps;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tps);

        // Inisialisasi komponen
        tvNamaTPS = findViewById(R.id.tvNamaTPS);
        tvAlamat = findViewById(R.id.tvAlamat);
        tvLokasi = findViewById(R.id.tvLokasi);
        tvKeterangan = findViewById(R.id.tvKeterangan);
        tvKapasitas = findViewById(R.id.tvKapasitas);
        tvJudul = findViewById(R.id.tvTitle);
        btnMaps = findViewById(R.id.btnMaps);
        btnBack = findViewById(R.id.btnBack);

        // Inisialisasi Map Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapPreview);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Ambil OBJEK Tps dari Intent
        Tps tps = (Tps) getIntent().getSerializableExtra("TPS_DATA");
        if (tps == null) {
            Toast.makeText(this, "Gagal memuat data TPS", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Simpan data penting
        tpsNama = tps.getNamaTps();
        String alamatLengkap = tps.getAlamat();
        String lokasiKoordinat = tps.getLokasi(); // Bisa jadi koordinat ATAU plus code

        // Tampilkan data
        tvJudul.setText(tpsNama);
        tvNamaTPS.setText(tpsNama);
        tvAlamat.setText("" + (alamatLengkap != null ? alamatLengkap : "-"));
        tvLokasi.setText("" + (lokasiKoordinat != null ? lokasiKoordinat : "-"));
        tvKeterangan.setText(tps.getKeterangan());
        tvKapasitas.setText("" + tps.getKapasitas());

        // Tombol buka Google Maps
        btnMaps.setOnClickListener(v -> {
            // Prioritaskan koordinat, jika tidak ada, gunakan alamat
            String query = (lokasiKoordinat != null && !lokasiKoordinat.isEmpty()) ? lokasiKoordinat : alamatLengkap;
            if (query != null && !query.isEmpty()) {
                try {
                    String mapQuery = "geo:0,0?q=" + Uri.encode(query);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapQuery));
                    startActivity(mapIntent);
                } catch (Exception e) {
                    Toast.makeText(this, "Gagal membuka peta", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Alamat lokasi tidak tersedia", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());

        // Panggil method pencari lokasi
        findLocationAsync(lokasiKoordinat, alamatLengkap);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        gMap.getUiSettings().setZoomControlsEnabled(true);
        gMap.getUiSettings().setScrollGesturesEnabled(false); // Matikan scroll

        // Jika koordinat sudah ditemukan, tampilkan
        if (tpsLatLng != null) {
            updateMapLocation();
        }
    }

    // --- ⬇️ METHOD PENCARI LOKASI YANG DIPERBARUI ⬇️ ---

    /**
     * Mencari LatLng. Coba parsing dulu, jika gagal, baru gunakan Geocoder.
     */
    private void findLocationAsync(String lokasi, String alamat) {
        // 1. Coba parsing koordinat (misal: "-7.60, 111.91")
        tpsLatLng = parseLatLng(lokasi);

        if (tpsLatLng != null) {
            // BERHASIL: Langsung update peta jika sudah siap
            Log.d("Lokasi", "Berhasil parse koordinat: " + tpsLatLng.toString());
            if (gMap != null) {
                runOnUiThread(this::updateMapLocation);
            }
            return; // Selesai
        }

        // 2. GAGAL PARSING: Gunakan Geocoder (di thread terpisah)
        // Kita gunakan 'lokasi' dulu (mungkin itu Plus Code), jika gagal, baru 'alamat'
        String stringUntukGeocoder = (lokasi != null && !lokasi.isEmpty()) ? lokasi : alamat;
        if (stringUntukGeocoder == null || stringUntukGeocoder.isEmpty()) {
            Log.w("Geocoder", "Alamat lokasi kosong, tidak bisa memuat peta.");
            return;
        }

        new Thread(() -> {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocationName(stringUntukGeocoder, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    tpsLatLng = new LatLng(address.getLatitude(), address.getLongitude());

                    // Pindahkan update UI kembali ke Main Thread
                    runOnUiThread(() -> {
                        if (gMap != null) {
                            updateMapLocation();
                        }
                    });
                } else {
                    Log.e("Geocoder", "Tidak ada alamat ditemukan untuk: " + stringUntukGeocoder);
                }
            } catch (IOException e) {
                Log.e("Geocoder", "Gagal Geocoding: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Method helper baru untuk mencoba mem-parsing string koordinat.
     * @return LatLng jika berhasil, null jika gagal.
     */
    private LatLng parseLatLng(String text) {
        if (text == null || text.isEmpty()) return null;

        // Ini adalah Regex untuk mencari pola "angka, angka" (dengan/tanpa spasi)
        Pattern pattern = Pattern.compile("(-?\\d+\\.\\d+)[, ]+(-?\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            try {
                double lat = Double.parseDouble(matcher.group(1));
                double lon = Double.parseDouble(matcher.group(2));
                return new LatLng(lat, lon);
            } catch (NumberFormatException e) {
                return null; // Gagal parsing
            }
        }
        return null; // Tidak cocok dengan pola
    }

    /**
     * Method baru untuk menambahkan Marker dan menggerakkan kamera peta
     */
    private void updateMapLocation() {
        if (gMap == null || tpsLatLng == null) return;

        gMap.clear();
        gMap.addMarker(new MarkerOptions().position(tpsLatLng).title(tpsNama));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tpsLatLng, 16f)); // Zoom 16x
    }
}