package id.polije.simpelsi.fitur;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import id.polije.simpelsi.CekStatusLaporan.CekStatusLaporanActivity;
import id.polije.simpelsi.api.ApiClient;
import id.polije.simpelsi.api.ApiInterface;
import id.polije.simpelsi.model.ResponseModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import id.polije.simpelsi.R;
import id.polije.simpelsi.Utils.FileUtils;
import id.polije.simpelsi.api.ApiClient;
import id.polije.simpelsi.model.ResponseModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PengajuanLaporanActivity extends AppCompatActivity {

    private EditText etNama, etLokasi, etKeterangan, etTanggal;
    private TextView tvUploadFileName;
    private Button btnUpload, btnHapusFoto;
    private ImageView btnBack, btnpilihtanggal, imgPreview;
    private LinearLayout uploadBox;
    private Uri imageUri;
    private File imageFile;
    private static final int PICK_IMAGE_REQUEST = 100;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengajuan_laporan);

        // Inisialisasi komponen
        etNama = findViewById(R.id.etNama);
        etLokasi = findViewById(R.id.etLokasi);
        etKeterangan = findViewById(R.id.etKeterangan);
        etTanggal = findViewById(R.id.etTanggal);
        tvUploadFileName = findViewById(R.id.tvUploadFileName);
        btnUpload = findViewById(R.id.btnUpload);
        btnHapusFoto = findViewById(R.id.btnHapusFoto);
        btnBack = findViewById(R.id.btnBack);
        btnpilihtanggal = findViewById(R.id.btnpilihtanggal);
        imgPreview = findViewById(R.id.imgPreview);
        uploadBox = findViewById(R.id.layoutUploadBox);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_pengajuan);

        // Pilih tanggal
        btnpilihtanggal.setOnClickListener(v -> showDatePicker());

        // Upload foto (klik box)
        uploadBox.setOnClickListener(v -> openFileChooser());

        // Tombol hapus
        btnHapusFoto.setOnClickListener(v -> {
            imageFile = null;
            imageUri = null;
            imgPreview.setImageDrawable(null);
            imgPreview.setVisibility(View.GONE);
            tvUploadFileName.setText("Format: .jpg / .png");
            Toast.makeText(this, "Foto dihapus", Toast.LENGTH_SHORT).show();
        });

        // Tombol upload
        btnUpload.setOnClickListener(v -> {
            if (validateForm()) uploadLaporan();
        });

        // Tombol kembali
        btnBack.setOnClickListener(v -> finish());

        // Bottom Navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_pengajuan) {
                return true;
            } else if (id == R.id.nav_profil) {
                startActivity(new Intent(this, ProfilActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                    etTanggal.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih foto"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            // Tampilan preview di imageview
            imgPreview.setVisibility(View.VISIBLE);
            imgPreview.setImageURI(imageUri); // ✅ perbaikan: gunakan setImageURI, bukan setImageDrawable

            // Tampilan nama file
            String fileName = getFileName(imageUri);
            tvUploadFileName.setText(fileName);

            String path = FileUtils.getPath(this, imageUri);
            if (path != null) {
                imageFile = new File(path);
                tvUploadFileName.setText(imageFile.getName());
            } else {
                Toast.makeText(this, "Gagal mengambil file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) { // ✅ huruf kecil 'cursor' & tambahkan kolom
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) { // ✅ tambahkan fallback untuk path file
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result; // ✅ tambahkan return
    }


    private boolean validateForm() {
        if (etNama.getText().toString().isEmpty()) {
            etNama.setError("Nama wajib diisi");
            return false;
        }
        if (etLokasi.getText().toString().isEmpty()) {
            etLokasi.setError("Lokasi wajib diisi");
            return false;
        }
        if (etKeterangan.getText().toString().isEmpty()) {
            etKeterangan.setError("Keterangan wajib diisi");
            return false;
        }
        if (etTanggal.getText().toString().isEmpty()) {
            Toast.makeText(this, "Tanggal belum dipilih", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (imageFile == null) {
            Toast.makeText(this, "Pilih foto terlebih dahulu", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void uploadLaporan() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Mengirim laporan...");
        pd.setCancelable(false);
        pd.show();

        // Ambil isi input dari form
        String namaStr = etNama.getText().toString().trim();
        String lokasiStr = etLokasi.getText().toString().trim();
        String ketStr = etKeterangan.getText().toString().trim();
        String tanggalStr = etTanggal.getText().toString().trim();

        // id masyarakat
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        String idMasyarakat = prefs.getString("id_masyarakat", null);

        if (idMasyarakat == null){
            pd.dismiss();
            Toast.makeText(this, "ID masyarakat tidak ditemukan. Silahkan Login ulang.", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestBody idBody = RequestBody.create(MultipartBody.FORM, idMasyarakat);

        // 🔹 Ubah format tanggal ke format MySQL (yyyy-MM-dd)
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = inputFormat.parse(tanggalStr);
            tanggalStr = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Format tanggal tidak valid", Toast.LENGTH_SHORT).show();
            pd.dismiss();
            return;
        }

        // Log untuk memastikan tanggal tidak kosong
        Log.d("UPLOAD_TANGGAL", "Tanggal dikirim: " + tanggalStr);
        Log.d("UPLOAD_DEBUG", "ID masyarakat" + idMasyarakat);

        // Siapkan body untuk dikirim ke server
        RequestBody nama = RequestBody.create(MultipartBody.FORM, namaStr);
        RequestBody lokasi = RequestBody.create(MultipartBody.FORM, lokasiStr);
        RequestBody keterangan = RequestBody.create(MultipartBody.FORM, ketStr);
        RequestBody tanggal = RequestBody.create(MultipartBody.FORM, tanggalStr);

        // Siapkan file foto
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part fotoPart = MultipartBody.Part.createFormData("foto", imageFile.getName(), reqFile);

        // Panggil API upload laporan
        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseModel> call = api.uploadLaporan(idBody,nama, lokasi, keterangan, tanggal, fotoPart);

        call.enqueue(new Callback<ResponseModel>() {

            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                pd.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(PengajuanLaporanActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    clearForm();
                } else {
                    Log.e("UploadError", "Code: " + response.code() + ", Message: " + response.message());
                    Log.e("UploadError", "Error body: " + response.errorBody());
                    Toast.makeText(PengajuanLaporanActivity.this, "Gagal upload laporan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(PengajuanLaporanActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



    private void clearForm() {
        etNama.setText("");
        etLokasi.setText("");
        etKeterangan.setText("");
        etTanggal.setText("");
        imageFile = null;
        tvUploadFileName.setText("Format: .jpg / .png");
    }
}
