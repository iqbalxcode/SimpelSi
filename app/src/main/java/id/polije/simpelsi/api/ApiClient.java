package id.polije.simpelsi.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // 🌐 Base URL API kamu — pastikan diakhiri dengan "/"
    public static final String BASE_URL = "https://simpelsi.pbltifnganjuk.com/api/";

    // Simpan instance Retrofit agar tidak dibuat ulang
    private static Retrofit retrofit = null;

    // 🔒 Method internal untuk buat Retrofit
    public static Retrofit getClient() {
        if (retrofit == null) {

            // ✅ Aktifkan GSON lenient agar tidak error parsing JSON
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            // 🔍 Logging untuk debugging (lihat request/response di Logcat)
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            // 🚀 Buat Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson)) // pakai GSON lenient
                    .build();
        }
        return retrofit;
    }

    // ✅ Method publik untuk digunakan di Activity
    public static ApiInterface getService() {
        return getClient().create(ApiInterface.class);
    }
}
