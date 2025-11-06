package id.polije.simpelsi.CekStatusLaporan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Model data untuk laporan masyarakat.
 * Mewakili satu entitas laporan yang dikirim dari/ke server.
 */
public class Laporan {

    @SerializedName("id_laporan")
    @Expose
    private String idLaporan;

    @SerializedName("id_masyarakat")
    @Expose
    private String idMasyarakat;

    @SerializedName("nama")
    @Expose
    private String nama;

    @SerializedName("lokasi")
    @Expose
    private String lokasi;

    @SerializedName("keterangan")
    @Expose
    private String keterangan;

    @SerializedName("tanggal")
    @Expose
    private String tanggal;

    @SerializedName("foto_url")
    @Expose
    private String fotoUrl;

    @SerializedName("status_laporan")
    @Expose
    private String statusLaporan;

    // === GETTER ===
    public String getIdLaporan() {
        return idLaporan;
    }

    public String getIdMasyarakat() {
        return idMasyarakat;
    }

    public String getNama() {
        return nama != null ? nama : "-";
    }

    public String getLokasi() {
        return lokasi != null ? lokasi : "-";
    }

    public String getKeterangan() {
        return keterangan != null ? keterangan : "-";
    }

    public String getTanggal() {
        return tanggal != null ? tanggal : "-";
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public String getStatusLaporan() {
        return statusLaporan != null ? statusLaporan : "-";
    }

    // === SETTER ===
    public void setIdLaporan(String idLaporan) {
        this.idLaporan = idLaporan;
    }

    public void setIdMasyarakat(String idMasyarakat) {
        this.idMasyarakat = idMasyarakat;
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

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public void setStatusLaporan(String statusLaporan) {
        this.statusLaporan = statusLaporan;
    }

    @Override
    public String toString() {
        return "Laporan{" +
                "idLaporan='" + idLaporan + '\'' +
                ", nama='" + nama + '\'' +
                ", lokasi='" + lokasi + '\'' +
                ", tanggal='" + tanggal + '\'' +
                ", statusLaporan='" + statusLaporan + '\'' +
                '}';
    }
}
