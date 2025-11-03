package id.polije.simpelsi.model; // ❗️ Pastikan package model Anda benar

import com.google.gson.annotations.SerializedName;

// ❗️ Nama class "LaporanModel" ini harus sesuai dengan yang Anda gunakan
//    di CekStatusLaporanActivity.java
//    (Misal: Call<ResponseLaporan> call = api.getLaporan(idMasyarakat);
//     ResponseLaporan harus berisi List<LaporanModel>)
public class LaporanModel {

    // ❗️ Tambahkan @SerializedName agar cocok dengan key JSON dari PHP

    @SerializedName("id_laporan") // ❗️ Tambahkan ID Laporan
    private String id_laporan;

    @SerializedName("nama")
    private String nama;

    @SerializedName("lokasi")
    private String lokasi;

    @SerializedName("keterangan")
    private String keterangan;

    @SerializedName("tanggal")
    private String tanggal;

    @SerializedName("foto") // Ini adalah nama file foto (misal: "gambar.jpg")
    private String foto;

    // ❗️ PERBAIKAN: Ubah "status" menjadi "status_laporan"
    @SerializedName("status_laporan")
    private String status_laporan;

    // --- Getter ---

    public String getId_laporan() { return id_laporan; }
    public String getNama() { return nama; }
    public String getLokasi() { return lokasi; }
    public String getKeterangan() { return keterangan; }
    public String getFoto() { return foto; }
    public String getTanggal() { return tanggal; }

    // ❗️ PERBAIKAN: Ubah getter "getStatus" menjadi "getStatus_laporan"
    public String getStatus_laporan() { return status_laporan; }
}