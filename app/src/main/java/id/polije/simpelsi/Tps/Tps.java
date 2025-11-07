package id.polije.simpelsi.Tps; // ⚠️ Sesuaikan package Anda

import com.google.gson.annotations.SerializedName;
import java.io.Serializable; // ❗️ Kita buat Serializable agar bisa dikirim

public class Tps implements Serializable {

    @SerializedName("id_tps")
    private String idTps;

    @SerializedName("nama_tps")
    private String namaTps;

    @SerializedName("lokasi") // Ini adalah koordinat/plus code (misal: CV4W+434)
    private String lokasi;

    @SerializedName("alamat") // Ini adalah alamat lengkap (misal: Jl. Wilangan)
    private String alamat;

    @SerializedName("kapasitas")
    private String kapasitas;

    @SerializedName("keterangan")
    private String keterangan;

    @SerializedName("foto_file") // Nama file gambar (misal: tps1.jpg)
    private String fotoFile;

    // --- Getters ---
    public String getIdTps() { return idTps; }
    public String getNamaTps() { return namaTps; }
    public String getLokasi() { return lokasi; }
    public String getAlamat() { return alamat; }
    public String getKapasitas() { return kapasitas; }
    public String getKeterangan() { return keterangan; }
    public String getFotoFile() { return fotoFile; }
}