package id.polije.simpelsi.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import id.polije.simpelsi.artikel.Artikel; // ❗️ Sesuaikan import Artikel.java

public class ResponseArtikel {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<Artikel> data;

    // --- Getters ---
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public List<Artikel> getData() { return data; }
}