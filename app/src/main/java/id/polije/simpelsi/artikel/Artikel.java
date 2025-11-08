package id.polije.simpelsi.artikel; // ⚠️ Sesuaikan package Anda

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Artikel implements Serializable {

    @SerializedName("id_artikel")
    private String idArtikel;

    @SerializedName("judul")
    private String judul;

    @SerializedName("deskripsi")
    private String deskripsi;

    // --- ⬇️ PERBAIKAN DI SINI ⬇️ ---
    @SerializedName("foto") // 1. Sesuaikan dengan key JSON "foto"
    private String foto; // 2. Ganti nama variabel
    // --- ⬆️ AKHIR PERBAIKAN ⬆️ ---

    @SerializedName("tanggal")
    private String tanggal;

    // --- Getters ---
    public String getIdArtikel() { return idArtikel; }
    public String getJudul() { return judul; }
    public String getDeskripsi() { return deskripsi; }
    public String getTanggal() { return tanggal; }

    // --- ⬇️ PERBAIKAN DI SINI ⬇️ ---
    // 3. Ganti nama method getter
    public String getFoto() {
        return foto;
    }
    // --- ⬆️ AKHIR PERBAIKAN ⬆️ ---
}