package id.polije.simpelsi.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import id.polije.simpelsi.CekStatusLaporan.Laporan;

public class ResponseLaporan {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<Laporan> data;

    // --- Getters ---
    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<Laporan> getData() {
        return data;
    }
}