package id.polije.simpelsi.api;

import androidx.annotation.Nullable;

import id.polije.simpelsi.model.*;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {

    @POST("login.php") 
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @FormUrlEncoded
    @POST("login_google.php")
    Call<LoginResponse> loginGoogle(
            @Field("google_id") String googleId,
            @Field("email") String email,
            @Field("nama") String nama
    );

    @POST("register.php")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @POST("request_otp.php")
    Call<OtpResponse> requestOtp(@Body OtpRequest request);

    @POST("verify_otp.php")
    Call<VerifyResponse> verifyOtp(@Body VerifyRequest request);

    @POST("reset_password.php")
    Call<ResetResponse> resetPassword(@Body ResetRequest request);

    // Ini adalah satu-satunya method upload yang benar
    @Multipart
    @POST("upload_laporan.php") // ❗️ PASTIKAN NAMA FILE INI BENAR
    Call<ResponseModel> uploadLaporan(
            @Part("id_masyarakat")  RequestBody id_masyarkat,
            @Part("nama") RequestBody nama,
            @Part("lokasi") RequestBody lokasi,
            @Part("keterangan") RequestBody keterangan,
            @Part("tanggal") RequestBody tanggal,
            @Part MultipartBody.Part foto
    );

    @GET("get_laporan.php")
    Call<ResponseLaporan> getLaporan(@Query("id_masyarakat") String idMasyarakat);


    @Multipart
    @POST("upload_laporan.php")
    Call<ResponseModel> uploadLaporan(
            @Part("id_masyarakat")  RequestBody id_masyarkat,
            // ... (parameter upload Anda)
            @Part MultipartBody.Part foto
    );

    // ⬇️ TAMBAHKAN DUA METHOD BARU INI ⬇️

    /**
     * Menghapus laporan (mengirim JSON)
     */
    @POST("hapus_laporan.php")
    Call<ResponseModel> tarikLaporan(@Body HapusRequest request);

    /**
     * Memperbarui laporan (mengirim Multipart)
     * Sama seperti upload, tapi DENGAN id_laporan
     */
    @Multipart
    @POST("update_laporan.php")
    Call<ResponseModel> updateLaporan(
            @Part("id_laporan") RequestBody idLaporan,
            @Part("id_masyarakat")  RequestBody idMasyarakat,
            @Part("nama") RequestBody nama,
            @Part("lokasi") RequestBody lokasi,
            @Part("keterangan") RequestBody keterangan,
            @Part("tanggal") RequestBody tanggal,
            @Part @Nullable MultipartBody.Part foto// ❗️ Foto dibuat opsional (@Nullable)

    );
    @GET("get_tps.php")
    Call<ResponseTps> getAllTps();
    @GET("get_artikel.php")
    Call<ResponseArtikel> getArtikel();
}