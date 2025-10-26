package id.polije.simpelsi.api;

import id.polije.simpelsi.model.LoginRequest;
import id.polije.simpelsi.model.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {

    // Menentukan endpoint API untuk login
    @POST("login.php")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    // Nanti Anda bisa tambahkan endpoint lain di sini
    // @GET("get_masyarakat.php")
    // Call<MasyarakatResponse> getAllMasyarakat();
}