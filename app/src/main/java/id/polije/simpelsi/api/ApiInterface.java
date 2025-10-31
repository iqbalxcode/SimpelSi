package id.polije.simpelsi.api;

import id.polije.simpelsi.model.LoginRequest;
import id.polije.simpelsi.model.LoginResponse;
import id.polije.simpelsi.model.OtpRequest;
import id.polije.simpelsi.model.OtpResponse;
import id.polije.simpelsi.model.RegisterRequest;
import id.polije.simpelsi.model.RegisterResponse;
import id.polije.simpelsi.model.ResetRequest;
import id.polije.simpelsi.model.ResetResponse;
import id.polije.simpelsi.model.ResponseLaporan;
import id.polije.simpelsi.model.ResponseModel;
import id.polije.simpelsi.model.VerifyRequest;
import id.polije.simpelsi.model.VerifyResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    @POST("login.php")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("google_login.php")
    Call<LoginResponse> loginGoogle(@Body LoginRequest googleRequest);

    @POST("register.php")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @POST("request_otp.php")
    Call<OtpResponse> requestOtp(@Body OtpRequest request);

    @POST("verify_otp.php")
    Call<VerifyResponse> verifyOtp(@Body VerifyRequest request);

    @POST("reset_password.php")
    Call<ResetResponse> resetPassword(@Body ResetRequest request);

    @Multipart
    @POST("upload_foto.php")
    Call<ResponseModel> uploadLaporan(
            @Part("nama") RequestBody nama,
            @Part("lokasi") RequestBody lokasi,
            @Part("keterangan") RequestBody keterangan,
            @Part("tanggal") RequestBody tanggal,
            @Part MultipartBody.Part foto
    );

    @GET("get_laporan.php")
    Call<ResponseLaporan> getLaporan();

}
