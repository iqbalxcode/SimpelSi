package id.polije.simpelsi.api;

import androidx.annotation.Nullable;

import id.polije.simpelsi.CekStatusLaporan.DetailResponse;
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

    // 🔹 LOGIN
    @POST("login.php")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @FormUrlEncoded
    @POST("login_google.php")
    Call<LoginResponse> loginGoogle(
            @Field("google_id") String googleId,
            @Field("email") String email,
            @Field("nama") String nama
    );

    // 🔹 REGISTER
    @POST("register.php")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    // 🔹 OTP
    @POST("request_otp.php")
    Call<OtpResponse> requestOtp(@Body OtpRequest request);

    @POST("verify_otp.php")
    Call<VerifyResponse> verifyOtp(@Body VerifyRequest request);

    @POST("reset_password.php")
    Call<ResetResponse> resetPassword(@Body ResetRequest request);

    // 🔹 UPLOAD LAPORAN
    @Multipart
    @POST("upload_laporan.php")
    Call<ResponseModel> uploadLaporan(
            @Part("id_masyarakat") RequestBody idMasyarakat,
            @Part("nama") RequestBody nama,
            @Part("lokasi") RequestBody lokasi,
            @Part("keterangan") RequestBody keterangan,
            @Part("tanggal") RequestBody tanggal,
            @Part MultipartBody.Part foto
    );

    // 🔹 GET LAPORAN BERDASARKAN ID PENGGUNA
    @GET("get_laporan.php")
    Call<ResponseLaporan> getLaporan(@Query("id_masyarakat") String idMasyarakat);

    // 🔹 HAPUS LAPORAN
    @POST("hapus_laporan.php")
    Call<ResponseModel> tarikLaporan(@Body HapusRequest request);

    // 🔹 UPDATE LAPORAN
    @Multipart
    @POST("update_laporan.php")
    Call<ResponseModel> updateLaporan(
            @Part("id_laporan") RequestBody idLaporan,
            @Part("id_masyarakat") RequestBody idMasyarakat,
            @Part("nama") RequestBody nama,
            @Part("lokasi") RequestBody lokasi,
            @Part("keterangan") RequestBody keterangan,
            @Part("tanggal") RequestBody tanggal,
            @Part @Nullable MultipartBody.Part foto
    );

    // 🔹 GET DATA TPS
    @GET("get_tps.php")
    Call<ResponseTps> getAllTps();

    // 🔹 GET ARTIKEL
    @GET("get_artikel.php")
    Call<ResponseArtikel> getArtikel();

    // 🔹 GET DETAIL LAPORAN (TERMASUK BALASAN)
    @GET("get_detail_laporan.php")
    Call<DetailResponse> getDetailLaporan(@Query("id_laporan") String idLaporan);


}
