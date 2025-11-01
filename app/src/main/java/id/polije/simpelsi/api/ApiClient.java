package id.polije.simpelsi.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ApiClient {

    // 🌐 Base URL API kamu
    public static final String BASE_URL = "http://simpelsi.medianewsonline.com/api/";

    // Simpan instance Retrofit agar tidak dibuat ulang
    private static Retrofit retrofit = null;

    // 🔒 Method internal untuk buat Retrofit
    public static Retrofit getClient() {
        if (retrofit == null) {
            // Logging request & response agar mudah debug
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // ✅ Method publik untuk digunakan di Activity
    public static ApiInterface getService() {
        return getClient().create(ApiInterface.class);
    }

}
