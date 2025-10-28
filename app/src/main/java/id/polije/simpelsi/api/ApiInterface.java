package id.polije.simpelsi.api;

// ... (import Anda yang lain)
import id.polije.simpelsi.model.LoginRequest;
import id.polije.simpelsi.model.LoginResponse;
import id.polije.simpelsi.model.RegisterRequest; // ❗️ TAMBAHKAN
import id.polije.simpelsi.model.RegisterResponse; // ❗️ TAMBAHKAN

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("login.php")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    // ❗️ ⬇️ TAMBAHKAN METHOD BARU INI ⬇️
    @POST("register.php")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);
}