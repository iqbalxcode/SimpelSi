package id.polije.simpelsi.api;

// Import yang tidak perlu sudah dihapus
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
// ❗️ (Saran: lihat bagian Opsional di bawah untuk import logging)
// import okhttp3.OkHttpClient;
// import okhttp3.logging.HttpLoggingInterceptor;


public class ApiClient {

    // Alamat dasar web server Anda yang baru
    public static final String BASE_URL = "http://simpelsi.medianewsonline.com/api/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {

            // ⚠️ Kode Interceptor, Cookie, dan User-Agent sudah dihapus ⚠️

            // ------------------------------------------------------------------
            // OPSIONAL: Sangat disarankan untuk menambahkan logging
            // Ini akan mencetak request & response API ke Logcat,
            // sangat membantu debugging!
            /*
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();
            */
            // ------------------------------------------------------------------

            // Bangun Retrofit (versi sederhana)
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    // .client(client) // <-- Hapus tanda comment ini jika Anda pakai logging
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}