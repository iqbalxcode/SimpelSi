package id.polije.simpelsi.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;


public class ApiClient {

    // --- ⬇️ PERBAIKAN DI SINI ⬇️ ---
    // Tambahkan "/api/" di akhir BASE_URL Anda
    public static final String BASE_URL = "http://simpelsi.medianewsonline.com/api/";
    // --- ⬆️ AKHIR PERBAIKAN ⬆️ ---

    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {

            // (Saran: Tetap gunakan logging ini untuk debugging)
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            // Bangun Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client) // <-- Menggunakan client dengan logging
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}