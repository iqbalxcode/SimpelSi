package id.polije.simpelsi.api;

import id.polije.simpelsi.model.LoginRequest;
import id.polije.simpelsi.model.LoginResponse;
import id.polije.simpelsi.model.OtpRequest;
import id.polije.simpelsi.model.OtpResponse;
import id.polije.simpelsi.model.RegisterRequest;
import id.polije.simpelsi.model.RegisterResponse;

import id.polije.simpelsi.model.ResetRequest;
import id.polije.simpelsi.model.ResetResponse;
import id.polije.simpelsi.model.VerifyRequest;
import id.polije.simpelsi.model.VerifyResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {

    // Login Manual (email + password)
    @POST("login.php")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    // Login Google (gunakan LoginRequest juga, karena field sama)
    @POST("google_login.php")
    Call<LoginResponse> loginGoogle(@Body LoginRequest googleRequest);

    // Register User
    @POST("register.php")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);
    @POST("request_otp.php")
    Call<OtpResponse> requestOtp(@Body OtpRequest request);

    @POST("verify_otp.php")
    Call<VerifyResponse> verifyOtp(@Body VerifyRequest request);

    @POST("reset_password.php")
    Call<ResetResponse> resetPassword(@Body ResetRequest request);
}