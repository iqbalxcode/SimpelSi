package id.polije.simpelsi.fitur;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.polije.simpelsi.R;
import id.polije.simpelsi.api.ApiClient;
import id.polije.simpelsi.api.ApiInterface;
import id.polije.simpelsi.CekStatusLaporan.Laporan; // ❗️ Pastikan import ini benar
import id.polije.simpelsi.model.ResponseModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditLaporanActivity extends AppCompatActivity {

    // Komponen UI
    private EditText etNama, etLokasi, etKeterangan, etTanggal;
    private TextView tvUploadFileName;
    // --- ⬇️ PERBAIKAN 1: UBAH TIPE DATA DARI TextView MENJADI Button ⬇️ ---
    private Button btnUpload, btnHapusFoto, tvGunakanLokasi; // ❗️ DIUBAH MENJADI BUTTON
    // --- ⬆️ AKHIR PERBAIKAN 1 ⬆️ ---
    private ImageView btnBack, btnpilihtanggal, imgPreview;
    private LinearLayout uploadBox;
    private BottomNavigationView bottomNavigationView;

    // ... (Variabel Foto/Kamera)
    private Uri imageUri;
    private Uri cameraFileUri;
    private File imageFile;
    private static final int PICK_IMAGE_GALLERY_REQUEST = 100;
    private static final int PICK_IMAGE_CAMERA_REQUEST = 101;
    private static final int PERMISSION_REQUEST_CODE_CAMERA = 200;

    private FusedLocationProviderClient fusedLocationClient;

    // Variabel Edit
    private Laporan laporan;
    private String idMasyarakat;
    private boolean fotoBaruDipilih = false;
    private ApiInterface apiInterface;

    private final ActivityResultLauncher<String> requestLocationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getCurrentLocation();
                } else {
                    Toast.makeText(this, "Izin lokasi ditolak", Toast.LENGTH_SHORT).show();
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengajuan_laporan);

        // Ambil data laporan dari Intent
        laporan = (Laporan) getIntent().getSerializableExtra("LAPORAN_DATA");

        // Ambil ID Masyarakat dari SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        idMasyarakat = prefs.getString("id_masyarakat", null);

        if (laporan == null || idMasyarakat == null) {
            Toast.makeText(this, "Gagal memuat data laporan. Coba lagi.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

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

        // --- ⬇️ PERBAIKAN 2: GUNAKAN ID YANG BENAR ⬇️ ---
        tvGunakanLokasi = findViewById(R.id.tvGunakanLokasi); // ❗️ ID Button
        // --- ⬆️ AKHIR PERBAIKAN 2 ⬆️ ---

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Sembunyikan BottomNav
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.GONE);
        }

        // Isi Form dengan Data Lama
        btnUpload.setText("Simpan Perubahan");
        etNama.setText(laporan.getNama());
        etLokasi.setText(laporan.getLokasi());
        etKeterangan.setText(laporan.getKeterangan());

        // Format & Isi Tanggal
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(laporan.getTanggal());
            etTanggal.setText(outputFormat.format(date));
        } catch (Exception e) {
            etTanggal.setText(laporan.getTanggal()); // Fallback
        }

        // Muat Foto Lama
        if (laporan.getFoto() != null && !laporan.getFoto().isEmpty()) {
            String urlProxy = ApiClient.BASE_URL + "get_image.php?file=" + laporan.getFoto();
            imgPreview.setVisibility(View.VISIBLE);
            tvUploadFileName.setVisibility(View.GONE);
            Glide.with(this)
                    .load(urlProxy)
                    .centerCrop()
                    .placeholder(R.drawable.loading)
                    .into(imgPreview);
        }

        // Setup Listeners
        btnpilihtanggal.setOnClickListener(v -> showDatePicker());
        uploadBox.setOnClickListener(v -> selectImageSource());
        btnHapusFoto.setOnClickListener(v -> {
            clearImageSelection(); // Hapus foto baru (jika ada)
            // Kembalikan ke foto lama
            Glide.with(this).load(ApiClient.BASE_URL + "get_image.php?file=" + laporan.getFoto()).into(imgPreview);
            fotoBaruDipilih = false;
        });
        btnBack.setOnClickListener(v -> finish());
        tvGunakanLokasi.setOnClickListener(v -> checkLocationPermissions());
        btnUpload.setOnClickListener(v -> {
            if (validateForm()) updateLaporan(); // Panggil method 'updateLaporan'
        });
    }

    // --- (SEMUA method helper: showDatePicker, selectImageSource, openGalleryChooser,
    //      checkCameraPermissions, onRequestPermissionsResult, openCamera, createImageFile,
    //      onActivityResult, displaySelectedImage, copyUriToCacheFile, getFileName,
    //      checkLocationPermissions, getCurrentLocation, getAddressFromLocation
    //      HARUS ADA DI SINI) ---

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

    private void selectImageSource() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Gambar")
                .setItems(new CharSequence[]{"Ambil dari Galeri", "Ambil Foto"}, (dialog, which) -> {
                    if (which == 0) {
                        openGalleryChooser();
                    } else {
                        checkCameraPermissions();
                    }
                });
        builder.create().show();
    }

    private void openGalleryChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Pilih foto dari galeri"), PICK_IMAGE_GALLERY_REQUEST);
    }

    private void checkCameraPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE_CAMERA);
            } else {
                openCamera();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE_CAMERA);
            } else {
                openCamera();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE_CAMERA) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                openCamera();
            } else {
                Toast.makeText(this, "Izin kamera atau penyimpanan ditolak", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            Log.e("Camera_Error", "Gagal membuat file gambar", ex);
            Toast.makeText(this, "Gagal menyiapkan kamera", Toast.LENGTH_SHORT).show();
        }

        if (photoFile != null) {
            cameraFileUri = FileProvider.getUriForFile(this,
                    "id.polije.simpelsi.fileprovider", // ❗️ Pastikan package name Anda benar
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri);
            startActivityForResult(takePictureIntent, PICK_IMAGE_CAMERA_REQUEST);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            fotoBaruDipilih = true; // ❗️ Tandai bahwa foto baru sudah dipilih

            if (requestCode == PICK_IMAGE_GALLERY_REQUEST && data != null && data.getData() != null) {
                imageUri = data.getData();
                String fileName = getFileName(imageUri);
                imageFile = copyUriToCacheFile(imageUri, fileName);
                if (imageFile != null) displaySelectedImage();
            } else if (requestCode == PICK_IMAGE_CAMERA_REQUEST) {
                imageUri = cameraFileUri;
                String fileName = getFileName(imageUri);
                imageFile = copyUriToCacheFile(imageUri, fileName);
                if (imageFile != null) displaySelectedImage();
            }
        }
    }

    private void displaySelectedImage() {
        if (imageUri != null) {
            imgPreview.setVisibility(View.VISIBLE);
            tvUploadFileName.setVisibility(View.GONE);
            imgPreview.setImageURI(imageUri);
        }
    }

    private File copyUriToCacheFile(Uri uri, String fileName) {
        try {
            File tempFile = new File(getCacheDir(), fileName);
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > 0) outputStream.write(buffer, 0, len);
            outputStream.close();
            inputStream.close();
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) { /* ... */ }
        }
        if (result == null) {
            result = uri.getPath();
            if (result != null) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) result = result.substring(cut + 1);
            }
        }
        if (result == null || result.isEmpty()) {
            result = "upload_" + System.currentTimeMillis() + ".jpg";
        }
        return result;
    }

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Toast.makeText(this, "Mencari lokasi...", Toast.LENGTH_SHORT).show();
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) getAddressFromLocation(location);
            else Toast.makeText(this, "Gagal mendapatkan lokasi. Pastikan GPS aktif.", Toast.LENGTH_SHORT).show();
        });
    }

    private void getAddressFromLocation(Location location) {
        if (!isFinishing() && !isDestroyed()) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null && !addresses.isEmpty()) {
                    etLokasi.setText(addresses.get(0).getAddressLine(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        return true;
    }

    private void updateLaporan() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Memperbarui laporan...");
        pd.setCancelable(false);
        pd.show();

        String namaStr = etNama.getText().toString().trim();
        String lokasiStr = etLokasi.getText().toString().trim();
        String ketStr = etKeterangan.getText().toString().trim();
        String tanggalStr = etTanggal.getText().toString().trim();

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = inputFormat.parse(tanggalStr);
            tanggalStr = outputFormat.format(date);
        } catch (Exception e) { e.printStackTrace(); }

        RequestBody idLaporanBody = RequestBody.create(MultipartBody.FORM, laporan.getIdLaporan());
        RequestBody idMasyarakatBody = RequestBody.create(MultipartBody.FORM, idMasyarakat);
        RequestBody namaBody = RequestBody.create(MultipartBody.FORM, namaStr);
        RequestBody lokasiBody = RequestBody.create(MultipartBody.FORM, lokasiStr);
        RequestBody ketBody = RequestBody.create(MultipartBody.FORM, ketStr);
        RequestBody tanggalBody = RequestBody.create(MultipartBody.FORM, tanggalStr);

        MultipartBody.Part fotoPart = null;
        if (fotoBaruDipilih && imageFile != null) {
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
            fotoPart = MultipartBody.Part.createFormData("foto", imageFile.getName(), reqFile);
            Log.d("UpdateLaporan", "Mengirim foto baru: " + imageFile.getName());
        } else {
            Log.d("UpdateLaporan", "Tidak ada foto baru dipilih.");
        }

        Call<ResponseModel> call = apiInterface.updateLaporan(idLaporanBody, idMasyarakatBody, namaBody, lokasiBody, ketBody, tanggalBody, fotoPart);

        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                pd.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(EditLaporanActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    if ("success".equals(response.body().getStatus())) {
                        finish();
                    }
                } else {
                    try {
                        Log.e("UpdateError", "Code: " + response.code() + ", Body: " + response.errorBody().string());
                    } catch (Exception e) { e.printStackTrace(); }
                    Toast.makeText(EditLaporanActivity.this, "Gagal update laporan (Server)", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(EditLaporanActivity.this, "Error Jaringan: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void clearForm() {
        etNama.setText("");
        etLokasi.setText("");
        etKeterangan.setText("");
        etTanggal.setText("");
        clearImageSelection();
    }

    private void clearImageSelection() {
        imageFile = null;
        imageUri = null;
        cameraFileUri = null;
        imgPreview.setImageDrawable(null);
        imgPreview.setVisibility(View.GONE);
        tvUploadFileName.setVisibility(View.VISIBLE);
        tvUploadFileName.setText("Format: .jpg / .png");
    }
}