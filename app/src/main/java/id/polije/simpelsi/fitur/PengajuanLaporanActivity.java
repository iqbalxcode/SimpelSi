package id.polije.simpelsi.fitur;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import android.widget.*;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

    private EditText etNama, etLokasiInput, etKeterangan, etTanggal;
    private TextView tvUploadFileName;
    private Button btnUpload, btnHapusFoto;
    private ImageView btnBack, btnPilihTanggal, imgPreview, btnLokasi;
    private LinearLayout uploadBox;
    private BottomNavigationView bottomNavigationView;

    private Uri imageUri, cameraFileUri;
    private File imageFile;
    private static final int PICK_IMAGE_GALLERY_REQUEST = 100;
    private static final int PICK_IMAGE_CAMERA_REQUEST = 101;
    private static final int PERMISSION_REQUEST_CODE_CAMERA = 200;

    private FusedLocationProviderClient fusedLocationClient;

    private final ActivityResultLauncher<String> requestLocationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) getCurrentLocation();
                else Toast.makeText(this, "Izin lokasi ditolak", Toast.LENGTH_SHORT).show();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengajuan_laporan);

        // Inisialisasi View
        etNama = findViewById(R.id.etNama);
        etTanggal = findViewById(R.id.etTanggal);
        etKeterangan = findViewById(R.id.etKeterangan);
        tvUploadFileName = findViewById(R.id.tvUploadFileName);
        btnUpload = findViewById(R.id.btnUpload);
        btnHapusFoto = findViewById(R.id.btnHapusFoto);
        btnBack = findViewById(R.id.btnBack);
        btnPilihTanggal = findViewById(R.id.btnpilihtanggal);
        imgPreview = findViewById(R.id.imgPreview);
        uploadBox = findViewById(R.id.layoutUploadBox);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Lokasi
        btnLokasi = findViewById(R.id.etLokasi); // ikon lokasi
        etLokasiInput = findViewById(R.id.Lokasi); // input lokasi (EditText di XML)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Tombol tanggal
        btnPilihTanggal.setOnClickListener(v -> showDatePicker());

        // Klik upload foto
        uploadBox.setOnClickListener(v -> selectImageSource());

        // Hapus foto
        btnHapusFoto.setOnClickListener(v -> {
            clearImageSelection();
            Toast.makeText(this, "Foto dihapus", Toast.LENGTH_SHORT).show();
        });

        // Klik upload laporan
        btnUpload.setOnClickListener(v -> {
            if (validateForm()) uploadLaporan();
        });

        // Klik tombol back
        btnBack.setOnClickListener(v -> finish());

        // Klik ikon lokasi atau field teks
        btnLokasi.setOnClickListener(v -> checkLocationPermissions());
        etLokasiInput.setOnClickListener(v -> checkLocationPermissions());

        // Bottom navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_pengajuan);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
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

    // ==================== DATE PICKER ====================
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, day) -> etTanggal.setText(day + "/" + (month + 1) + "/" + year),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    // ==================== AMBIL FOTO ====================
    private void selectImageSource() {
        new AlertDialog.Builder(this)
                .setTitle("Pilih Gambar")
                .setItems(new CharSequence[]{"Ambil dari Galeri", "Ambil Foto"}, (dialog, which) -> {
                    if (which == 0) openGalleryChooser();
                    else checkCameraPermissions();
                })
                .show();
    }

    private void openGalleryChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Pilih foto"), PICK_IMAGE_GALLERY_REQUEST);
    }

    private void checkCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE_CAMERA);
        } else openCamera();
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File photoFile = createImageFile();
            if (photoFile != null) {
                cameraFileUri = FileProvider.getUriForFile(this,
                        "id.polije.simpelsi.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri);
                startActivityForResult(takePictureIntent, PICK_IMAGE_CAMERA_REQUEST);
            }
        } catch (IOException e) {
            Toast.makeText(this, "Gagal membuka kamera", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile("JPEG_" + timeStamp + "_", ".jpg", storageDir);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_GALLERY_REQUEST && data != null && data.getData() != null) {
                imageUri = data.getData();
                String fileName = getFileName(imageUri);
                imageFile = copyUriToCacheFile(imageUri, fileName);
                if (imageFile != null) displaySelectedImage();
            } else if (requestCode == PICK_IMAGE_CAMERA_REQUEST) {
                imageUri = cameraFileUri;
                imageFile = copyUriToCacheFile(imageUri, getFileName(imageUri));
                if (imageFile != null) displaySelectedImage();
            }
        }
    }

    private void displaySelectedImage() {
        imgPreview.setVisibility(View.VISIBLE);
        tvUploadFileName.setVisibility(View.GONE);
        imgPreview.setImageURI(imageUri);
    }

    private File copyUriToCacheFile(Uri uri, String fileName) {
        try {
            File tempFile = new File(getCacheDir(), fileName);
            InputStream inputStream = getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > 0) outputStream.write(buffer, 0, len);
            outputStream.close();
            inputStream.close();
            return tempFile;
        } catch (IOException e) {
            Log.e("File_Copy_Error", "Gagal salin file: " + e.getMessage());
            return null;
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst())
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) result = result.substring(cut + 1);
        }
        return result;
    }

    // ==================== LOKASI ====================
    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) getCurrentLocation();
        else requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        Toast.makeText(this, "Mengambil lokasi...", Toast.LENGTH_SHORT).show();
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) getAddressFromLocation(location);
                    else Toast.makeText(this, "Aktifkan GPS untuk hasil akurat", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(this, e ->
                        Toast.makeText(this, "Gagal mendapatkan lokasi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty())
                etLokasiInput.setText(addresses.get(0).getAddressLine(0));
            else etLokasiInput.setText("Alamat tidak ditemukan");
        } catch (IOException e) {
            etLokasiInput.setText("Gagal mengonversi lokasi");
        }
    }

    // ==================== VALIDASI & UPLOAD ====================
    private boolean validateForm() {
        if (etNama.getText().toString().isEmpty()) {
            etNama.setError("Nama wajib diisi");
            return false;
        }
        if (etLokasiInput.getText().toString().isEmpty()) {
            etLokasiInput.setError("Lokasi wajib diisi");
            return false;
        }
        if (etKeterangan.getText().toString().isEmpty()) {
            etKeterangan.setError("Keterangan wajib diisi");
            return false;
        }
        if (etTanggal.getText().toString().isEmpty()) {
            Toast.makeText(this, "Pilih tanggal", Toast.LENGTH_SHORT).show();
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

        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        String idMasyarakat = prefs.getString("id_masyarakat", null);
        if (idMasyarakat == null) {
            pd.dismiss();
            Toast.makeText(this, "Silakan login ulang", Toast.LENGTH_SHORT).show();
            return;
        }

        String tanggalStr = etTanggal.getText().toString();
        try {
            SimpleDateFormat input = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = input.parse(tanggalStr);
            tanggalStr = output.format(date);
        } catch (Exception e) {
            Toast.makeText(this, "Format tanggal salah", Toast.LENGTH_SHORT).show();
            pd.dismiss();
            return;
        }

        RequestBody id = RequestBody.create(MultipartBody.FORM, idMasyarakat);
        RequestBody nama = RequestBody.create(MultipartBody.FORM, etNama.getText().toString());
        RequestBody lokasi = RequestBody.create(MultipartBody.FORM, etLokasiInput.getText().toString());
        RequestBody ket = RequestBody.create(MultipartBody.FORM, etKeterangan.getText().toString());
        RequestBody tgl = RequestBody.create(MultipartBody.FORM, tanggalStr);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part foto = MultipartBody.Part.createFormData("foto", imageFile.getName(), reqFile);

        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);
        api.uploadLaporan(id, nama, lokasi, ket, tgl, foto)
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        pd.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(PengajuanLaporanActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            clearForm();
                        } else {
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
        etLokasiInput.setText("");
        etKeterangan.setText("");
        etTanggal.setText("");
        clearImageSelection();
    }

    private void clearImageSelection() {
        imageFile = null;
        imageUri = null;
        cameraFileUri = null;
        imgPreview.setVisibility(View.GONE);
        tvUploadFileName.setVisibility(View.VISIBLE);
        tvUploadFileName.setText("Format: .jpg / .png");
    }
}
