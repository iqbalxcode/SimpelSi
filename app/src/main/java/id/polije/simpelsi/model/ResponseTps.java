package id.polije.simpelsi.model; // ⚠️ Sesuaikan package Anda

import com.google.gson.annotations.SerializedName;
import java.util.List;
import id.polije.simpelsi.Tps.Tps; // ❗️ Sesuaikan import Tps.java Anda

public class ResponseTps {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<Tps> data;

    // --- Getters ---
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public List<Tps> getData() { return data; }
}