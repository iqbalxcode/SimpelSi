package id.polije.simpelsi.CekStatusLaporan;

// ❗️ 1. Tambahkan import ini
import com.google.gson.annotations.SerializedName;

public class Laporan {

    // ❗️ 2. Tambahkan anotasi @SerializedName agar cocok dengan JSON dari PHP

    @SerializedName("id_laporan")
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

    @SerializedName("status_laporan") // Ini adalah key status dari PHP
    private String status_laporan;

    // ❗️ 3. Variabel "status" yang ganda sudah dihapus

    // --- Getter ---
    public String getId_laporan() {
        return id_laporan;
    }

    public String getNama() {
        return nama;
    }

    public String getLokasi() {
        return lokasi;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getFoto() {
        return foto;
    }

    public String getStatus_laporan() {
        return status_laporan;
    }

    // ❗️ 4. Getter "getStatus()" yang ganda sudah dihapus

    // --- Setter (Opsional, tidak masalah jika ada) ---
    public void setId_laporan(String id_laporan) {
        this.id_laporan = id_laporan;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public void setStatus_laporan(String status_laporan) {
        this.status_laporan = status_laporan;
    }
}