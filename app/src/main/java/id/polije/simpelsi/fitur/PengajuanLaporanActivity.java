package id.polije.simpelsi.fitur;

import android.Manifest;
import android.app.AlertDialog; // ❗️ Import
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager; // ❗️ Import
import android.database.Cursor;
import android.net.Uri;
import android.os.Build; // ❗️ Import
import android.os.Bundle;
import android.os.Environment; // ❗️ Import
import android.provider.MediaStore; // ❗️ Import
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat; // ❗️ Import
import androidx.core.content.ContextCompat; // ❗️ Import
import androidx.core.content.FileProvider; // ❗️ Import

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    private EditText etNama, etLokasi, etKeterangan, etTanggal;
    private TextView tvUploadFileName;
    private Button btnUpload, btnHapusFoto;
    private ImageView btnBack, btnpilihtanggal, imgPreview;
    private LinearLayout uploadBox;
    private Uri imageUri; // Uri dari galeri
    private Uri cameraFileUri; // Uri untuk file dari kamera
    private File imageFile; // File yang akan diupload (dari galeri/kamera)
    private static final int PICK_IMAGE_GALLERY_REQUEST = 100; // Untuk galeri
    private static final int PICK_IMAGE_CAMERA_REQUEST = 101; // Untuk kamera
    private static final int PERMISSION_REQUEST_CODE = 200; // Untuk izin kamera

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengajuan_laporan);

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

        btnpilihtanggal.setOnClickListener(v -> showDatePicker());
        // ❗️ Ganti openFileChooser() menjadi selectImageSource()
        uploadBox.setOnClickListener(v -> selectImageSource());
        btnHapusFoto.setOnClickListener(v -> {
            clearImageSelection(); // Menggunakan method baru
            Toast.makeText(this, "Foto dihapus", Toast.LENGTH_SHORT).show();
        });
        btnUpload.setOnClickListener(v -> {
            if (validateForm()) uploadLaporan();
        });
        btnBack.setOnClickListener(v -> finish());
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

    // --- ⬇️ METHOD BARU UNTUK PILIH SUMBER GAMBAR ⬇️ ---
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
        // Untuk Android 13 (API 33) ke atas, hanya perlu izin kamera
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
            } else {
                openCamera();
            }
        } else { // Untuk Android < 13, perlu CAMERA dan WRITE_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                openCamera();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
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
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(); // Membuat file kosong untuk gambar
            } catch (IOException ex) {
                Log.e("Camera_Error", "Gagal membuat file gambar", ex);
                Toast.makeText(this, "Gagal menyiapkan kamera", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                // Mendapatkan URI yang aman menggunakan FileProvider
                cameraFileUri = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri);
                startActivityForResult(takePictureIntent, PICK_IMAGE_CAMERA_REQUEST);
            }
        } else {
            Toast.makeText(this, "Tidak ada aplikasi kamera ditemukan", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        // Membuat nama file gambar yang unik
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // Dapatkan direktori penyimpanan eksternal untuk gambar aplikasi
        // Ini akan berada di: /sdcard/Android/data/id.polije.simpelsi/files/Pictures
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Buat file gambar
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }
    // --- ⬆️ AKHIR METHOD BARU ⬆️ ---


    // --- ⬇️ onActivityResult yang diperbarui untuk GALERI & KAMERA ⬇️ ---
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_GALLERY_REQUEST && data != null && data.getData() != null) {
                // Hasil dari galeri
                imageUri = data.getData();
                String fileName = getFileName(imageUri);
                imageFile = copyUriToCacheFile(imageUri, fileName); // Salin dari galeri ke cache

                if (imageFile != null) {
                    displaySelectedImage();
                } else {
                    Toast.makeText(this, "Gagal mengambil file dari galeri", Toast.LENGTH_SHORT).show();
                    clearImageSelection();
                }
            } else if (requestCode == PICK_IMAGE_CAMERA_REQUEST) {
                // Hasil dari kamera
                imageUri = cameraFileUri; // Gunakan Uri yang kita buat untuk kamera

                // imageFile sudah langsung menunjuk ke file yang dibuat createImageFile()
                // karena cameraFileUri dibuat dari FileProvider yang menunjuk ke file tersebut.
                // Tidak perlu copy lagi.
                imageFile = new File(imageUri.getPath());
                // Catatan: getPath() dari FileProvider URI kadang tidak langsung file path.
                // Lebih aman kita simpan referensi ke file objek saat createImageFile().
                // Untuk kesederhanaan, kita asumsikan cameraFileUri.getPath() bisa diakses.
                // Namun, cara terbaik adalah menyimpan 'photoFile' yang dibuat oleh createImageFile()
                // sebagai variabel member.

                // Untuk memastikan, mari kita gunakan pendekatan yang lebih aman
                // yaitu membuat File objek dari URI kamera.
                // Ini mungkin masih perlu diuji coba di berbagai perangkat.
                // Jika getPath() tidak bekerja, Anda bisa menyimpan 'photoFile' dari createImageFile()
                // sebagai member variable seperti 'currentPhotoFile'.

                // Untuk saat ini, kita akan mencoba mendapatkan file dari URI kamera
                // dan jika tidak berhasil, kembali ke clearImageSelection().
                File tempCameraFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), getFileName(imageUri));
                if(tempCameraFile.exists() && tempCameraFile.length() > 0) {
                    imageFile = tempCameraFile; // Gunakan file kamera yang kita simpan
                } else {
                    // Fallback jika file tidak ditemukan melalui getPath(), coba cari berdasarkan nama
                    String cameraFileName = getFileName(cameraFileUri);
                    File storedImage = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), cameraFileName);
                    if (storedImage.exists() && storedImage.length() > 0) {
                        imageFile = storedImage;
                    } else {
                        Toast.makeText(this, "Gagal mengambil foto dari kamera. Coba lagi.", Toast.LENGTH_SHORT).show();
                        clearImageSelection();
                        return;
                    }
                }
                displaySelectedImage(); // Tampilkan gambar dari kamera
            }
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Pengambilan gambar dibatalkan.", Toast.LENGTH_SHORT).show();
            // Jika dibatalkan, pastikan imageUri/imageFile bersih
            clearImageSelection();
        } else {
            Toast.makeText(this, "Gagal mengambil gambar.", Toast.LENGTH_SHORT).show();
            clearImageSelection();
        }
    }
    // --- ⬆️ AKHIR onActivityResult ⬆️ ---

    // --- ⬇️ Method displaySelectedImage BARU ⬇️ ---
    private void displaySelectedImage() {
        if (imageUri != null) {
            imgPreview.setVisibility(View.VISIBLE);
            imgPreview.setImageURI(imageUri);
            if (imageFile != null) {
                tvUploadFileName.setText(imageFile.getName());
            } else {
                tvUploadFileName.setText(getFileName(imageUri));
            }
        }
    }
    // --- ⬆️ AKHIR Method displaySelectedImage ⬆️ ---

    private File copyUriToCacheFile(Uri uri, String fileName) {
        // ... (Kode Anda sudah benar, tidak perlu diubah) ...
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
        // ... (Kode Anda sudah benar, tidak perlu diubah) ...
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

    private boolean validateForm() {
        // ... (Kode Anda sudah benar) ...
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
        // ... (Kode Anda sudah benar) ...
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Mengirim laporan...");
        pd.setCancelable(false);
        pd.show();

        String namaStr = etNama.getText().toString().trim();
        String lokasiStr = etLokasi.getText().toString().trim();
        String ketStr = etKeterangan.getText().toString().trim();
        String tanggalStr = etTanggal.getText().toString().trim();

        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
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
        clearImageSelection(); // Menggunakan method baru
    }

    // --- ⬇️ Method clearImageSelection BARU ⬇️ ---
    private void clearImageSelection() {
        imageFile = null;
        imageUri = null;
        cameraFileUri = null; // Pastikan juga URI kamera direset
        imgPreview.setImageDrawable(null);
        imgPreview.setVisibility(View.GONE);
        tvUploadFileName.setText("Format: .jpg / .png");
    }
    // --- ⬆️ AKHIR Method clearImageSelection ⬆️ ---
}