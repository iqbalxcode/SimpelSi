package id.polije.simpelsi.model;

// ❗️ 1. Tambahkan import untuk SerializedName
import com.google.gson.annotations.SerializedName;
import java.util.List;
import id.polije.simpelsi.CekStatusLaporan.Laporan;

public class ResponseLaporan {

    // ❗️ 2. Tambahkan anotasi untuk semua field
    @SerializedName("status")
    private String status;

    // ❗️ 3. Tambahkan field "message" yang hilang (sesuai JSON dari PHP)
    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<Laporan> data;

    // --- Getters ---

    public String getStatus() {
        return status;
    }

    // ❗️ 4. Tambahkan getter "getMessage" yang hilang
    public String getMessage() {
        return message;
    }

    public List<Laporan> getData() {
        return data;
    }
}