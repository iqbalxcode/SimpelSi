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
import android.location.Address; // ❗️ Import Lokasi
import android.location.Geocoder; // ❗️ Import Lokasi
import android.location.Location; // ❗️ Import Lokasi
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

import androidx.activity.result.ActivityResultLauncher; // ❗️ Import Launcher
import androidx.activity.result.contract.ActivityResultContracts; // ❗️ Import Launcher
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient; // ❗️ Import Lokasi
import com.google.android.gms.location.LocationServices; // ❗️ Import Lokasi
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List; // ❗️ Import Lokasi
import java.util.Locale;

import id.polije.simpelsi.R;
import id.polije.simpelsi.api.ApiClient;
import id.polije.simpelsi.api.ApiInterface;
import id.polije.simpelsi.model.ResponseModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PengajuanLaporanActivity extends AppCompatActivity {

    // Komponen UI
    private EditText etNama, etLokasi, etKeterangan, etTanggal;
    private TextView tvUploadFileName;
    private Button btnUpload, btnHapusFoto;
    private ImageView btnBack, btnpilihtanggal, imgPreview;
    private LinearLayout uploadBox;
    private BottomNavigationView bottomNavigationView;

    // Variabel untuk Foto/Kamera
    private Uri imageUri;
    private Uri cameraFileUri;
    private File imageFile;
    private static final int PICK_IMAGE_GALLERY_REQUEST = 100;
    private static final int PICK_IMAGE_CAMERA_REQUEST = 101;
    private static final int PERMISSION_REQUEST_CODE_CAMERA = 200; // Ganti nama agar unik

    // --- ⬇️ VARIABEL BARU UNTUK LOKASI ⬇️ ---
    private TextView tvGunakanLokasi;
    private FusedLocationProviderClient fusedLocationClient;

    /**
     * Launcher baru untuk meminta izin lokasi (ACCESS_FINE_LOCATION)
     */
    private final ActivityResultLauncher<String> requestLocationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Izin diberikan
                    getCurrentLocation();
                } else {
                    // Izin ditolak
                    Toast.makeText(this, "Izin lokasi ditolak", Toast.LENGTH_SHORT).show();
                }
            });
    // --- ⬆️ AKHIR VARIABEL BARU ⬆️ ---


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengajuan_laporan);

        // Inisialisasi komponen UI
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

        // --- ⬇️ INISIALISASI BARU ⬇️ ---
        tvGunakanLokasi = findViewById(R.id.tvGunakanLokasi); // ❗️ ID Button/TextView
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // --- ⬆️ AKHIR INISIALISASI BARU ⬆️ ---

        // Setup Listeners
        btnpilihtanggal.setOnClickListener(v -> showDatePicker());
        uploadBox.setOnClickListener(v -> selectImageSource());
        btnHapusFoto.setOnClickListener(v -> {
            clearImageSelection();
            Toast.makeText(this, "Foto dihapus", Toast.LENGTH_SHORT).show();
        });
        btnUpload.setOnClickListener(v -> {
            if (validateForm()) uploadLaporan();
        });
        btnBack.setOnClickListener(v -> finish());

        // --- ⬇️ LISTENER BARU UNTUK TOMBOL LOKASI ⬇️ ---
        tvGunakanLokasi.setOnClickListener(v -> {
            checkLocationPermissions();
        });
        // --- ⬆️ AKHIR LISTENER BARU ⬆️ ---

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

    // --- (Method untuk pilih Galeri/Kamera Anda sudah benar) ---
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
        // Ganti nama konstanta agar tidak bentrok
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

    // Ganti nama konstanta agar tidak bentrok
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Cek apakah ini hasil dari izin KAMERA
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
            if (requestCode == PICK_IMAGE_GALLERY_REQUEST && data != null && data.getData() != null) {
                // Hasil dari galeri
                imageUri = data.getData();
                String fileName = getFileName(imageUri);
                imageFile = copyUriToCacheFile(imageUri, fileName);
                if (imageFile != null) {
                    displaySelectedImage();
                } else {
                    Toast.makeText(this, "Gagal mengambil file dari galeri", Toast.LENGTH_SHORT).show();
                    clearImageSelection();
                }
            } else if (requestCode == PICK_IMAGE_CAMERA_REQUEST) {
                // Hasil dari kamera
                imageUri = cameraFileUri;
                // Salin dari URI FileProvider ke cache agar konsisten
                String fileName = getFileName(imageUri);
                imageFile = copyUriToCacheFile(imageUri, fileName);

                if (imageFile != null) {
                    displaySelectedImage();
                } else {
                    Toast.makeText(this, "Gagal menyimpan foto dari kamera", Toast.LENGTH_SHORT).show();
                    clearImageSelection();
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            // Pengguna membatalkan
            // clearImageSelection(); // Opsional
        }
    }

    private void displaySelectedImage() {
        if (imageUri != null) {
            imgPreview.setVisibility(View.VISIBLE);
            tvUploadFileName.setVisibility(View.GONE); // ❗️ Sembunyikan teks "Format: .jpg"
            imgPreview.setImageURI(imageUri);
        }
    }

    private File copyUriToCacheFile(Uri uri, String fileName) {
        try {
            File tempFile = new File(getCacheDir(), fileName);
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                Log.e("File_Copy_Error", "InputStream null");
                return null;
            }
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.close();
            inputStream.close();
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("File_Copy_Error", "Gagal menyalin file ke cache: " + e.getMessage());
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
            } catch (Exception e) {
                Log.e("GetFileName", "Error saat query nama file: " + e.getMessage());
            }
        }
        if (result == null) {
            result = uri.getPath();
            if (result != null) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
        }
        if (result == null || result.isEmpty()) {
            result = "upload_" + System.currentTimeMillis() + ".jpg";
        }
        return result;
    }


    // --- ⬇️ METHOD BARU UNTUK LOKASI ⬇️ ---

    /**
     * Memeriksa izin lokasi. Jika diizinkan, ambil lokasi. Jika tidak, minta izin.
     */
    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Izin sudah ada, langsung ambil lokasi
            getCurrentLocation();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Tampilkan dialog penjelasan mengapa Anda butuh izin ini
            new AlertDialog.Builder(this)
                    .setTitle("Izin Lokasi Diperlukan")
                    .setMessage("Aplikasi ini memerlukan izin lokasi untuk mengisi alamat secara otomatis.")
                    .setPositiveButton("OK", (dialog, which) -> {
                        // Minta izin
                        requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                    })
                    .setNegativeButton("Batal", (dialog, which) -> dialog.dismiss())
                    .create().show();
        } else {
            // Minta izin
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Mengambil lokasi terakhir yang diketahui.
     */
    private void getCurrentLocation() {
        // Cek ulang izin (wajib oleh Android)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Izin lokasi tidak ada", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Mencari lokasi...", Toast.LENGTH_SHORT).show();

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        // Lokasi berhasil didapat, ubah menjadi alamat
                        getAddressFromLocation(location);
                    } else {
                        // Ini sering terjadi jika GPS baru diaktifkan atau di emulator
                        Toast.makeText(this, "Gagal mendapatkan lokasi. Pastikan GPS aktif dan coba lagi.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.e("Location_Error", "Gagal mendapatkan lokasi: " + e.getMessage());
                    Toast.makeText(this, "Gagal mendapatkan lokasi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Mengubah koordinat (Lat/Lon) menjadi alamat jalan (Reverse Geocoding).
     */
    private void getAddressFromLocation(Location location) {
        // Geocoder butuh Context, pastikan Activity masih ada
        if (!isFinishing() && !isDestroyed()) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                // Ambil 1 alamat dari koordinat
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);

                    // address.getAddressLine(0) adalah alamat lengkap (misal: "Jl. Merdeka 10, Nganjuk, ...")
                    String fullAddress = address.getAddressLine(0);

                    // Set teks di EditText
                    etLokasi.setText(fullAddress);

                } else {
                    etLokasi.setText("Alamat tidak ditemukan dari GPS");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Geocoder_Error", "Gagal melakukan geocoding: " + e.getMessage());
                etLokasi.setText("Gagal mendapatkan alamat (Error Jaringan)");
            }
        }
    }
    // --- ⬆️ AKHIR METHOD LOKASI ⬆️ ---


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

        String namaStr = etNama.getText().toString().trim();
        String lokasiStr = etLokasi.getText().toString().trim();
        String ketStr = etKeterangan.getText().toString().trim();
        String tanggalStr = etTanggal.getText().toString().trim();

        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        // ❗️ Pastikan key ini "id_masyarakat" (sesuai perbaikan kita sebelumnya)
        String idMasyarakat = prefs.getString("id_masyarakat", null);

        if (idMasyarakat == null){
            pd.dismiss();
            Toast.makeText(this, "ID masyarakat tidak ditemukan. Silahkan Login ulang.", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestBody idBody = RequestBody.create(MultipartBody.FORM, idMasyarakat);

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

        Log.d("UPLOAD_TANGGAL", "Tanggal dikirim: " + tanggalStr);
        Log.d("UPLOAD_DEBUG", "ID masyarakat" + idMasyarakat);

        RequestBody nama = RequestBody.create(MultipartBody.FORM, namaStr);
        RequestBody lokasi = RequestBody.create(MultipartBody.FORM, lokasiStr);
        RequestBody keterangan = RequestBody.create(MultipartBody.FORM, ketStr);
        RequestBody tanggal = RequestBody.create(MultipartBody.FORM, tanggalStr);

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part fotoPart = MultipartBody.Part.createFormData("foto", imageFile.getName(), reqFile);

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
                    try {
                        Log.e("UploadError", "Error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
        clearImageSelection();
    }

    private void clearImageSelection() {
        imageFile = null;
        imageUri = null;
        cameraFileUri = null;
        imgPreview.setImageDrawable(null);
        imgPreview.setVisibility(View.GONE);
        // ❗️ Tampilkan kembali teks placeholder
        tvUploadFileName.setVisibility(View.VISIBLE);
        tvUploadFileName.setText("Format: .jpg / .png");
    }
}