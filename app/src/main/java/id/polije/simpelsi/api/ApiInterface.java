package id.polije.simpelsi.api;

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

    // 🔐 Login
    @POST("login.php")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @FormUrlEncoded
    @POST("login_google.php")
    Call<LoginResponse> loginGoogle(
            @Field("google_id") String googleId,
            @Field("email") String email,
            @Field("nama") String nama
    );

    // 📝 Register & OTP
    @POST("register.php")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @POST("request_otp.php")
    Call<OtpResponse> requestOtp(@Body OtpRequest request);

    @POST("verify_otp.php")
    Call<VerifyResponse> verifyOtp(@Body VerifyRequest request);

    @POST("reset_password.php")
    Call<ResetResponse> resetPassword(@Body ResetRequest request);

    // 📤 Upload Laporan
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

    // 📥 Ambil Laporan
    @GET("get_laporan.php")
    Call<ResponseLaporan> getLaporan(@Query("id_masyarakat") String idMasyarakat);
}