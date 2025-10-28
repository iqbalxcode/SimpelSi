package id.polije.simpelsi.api;

// ... (import Anda yang lain)
import id.polije.simpelsi.model.LoginRequest;
import id.polije.simpelsi.model.LoginResponse;
import id.polije.simpelsi.model.OtpRequest;
import id.polije.simpelsi.model.OtpResponse;
import id.polije.simpelsi.model.RegisterRequest; // ❗️ TAMBAHKAN
import id.polije.simpelsi.model.RegisterResponse; // ❗️ TAMBAHKAN

import id.polije.simpelsi.model.ResetRequest;
import id.polije.simpelsi.model.ResetResponse;
import id.polije.simpelsi.model.VerifyRequest;
import id.polije.simpelsi.model.VerifyResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("login.php")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    // ❗️ ⬇️ TAMBAHKAN METHOD BARU INI ⬇️
    @POST("register.php")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);
    @POST("request_otp.php")
    Call<OtpResponse> requestOtp(@Body OtpRequest request);

    @POST("verify_otp.php")
    Call<VerifyResponse> verifyOtp(@Body VerifyRequest request);

    @POST("reset_password.php")
    Call<ResetResponse> resetPassword(@Body ResetRequest request);
}