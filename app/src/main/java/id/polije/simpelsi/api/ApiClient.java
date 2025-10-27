package id.polije.simpelsi.api;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // Alamat dasar web server Anda
    public static final String BASE_URL = "https://simpelsi.byethost31.com/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {

            // ⚠️ INTERCEPTOR UNTUK MASALAH BYETHOST ⚠️
            Interceptor headerInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    // Salin Cookie dan User-Agent dari browser Anda
                    // Ini harus diganti setiap kali cookie kedaluwarsa!
                    String cookie = "__test=90217758eae290f298b28406f44b229a"; // ⚠️ GANTI COOKIE INI
                    String userAgent = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Mobile Safari/537.36"; // ⚠️ GANTI USER-AGENT INI

                    Request request = original.newBuilder()
                            .header("User-Agent", userAgent)
                            .header("Cookie", cookie)
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            };

            // Buat OkHttpClient dan tambahkan Interceptor
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(headerInterceptor)
                    .build();

            // Bangun Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client) // <-- Gunakan client kustom
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}