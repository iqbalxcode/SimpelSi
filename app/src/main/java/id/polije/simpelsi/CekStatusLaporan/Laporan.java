package id.polije.simpelsi.CekStatusLaporan;

public class Laporan {
    private String id_laporan;
    private String nama;
    private String lokasi;
    private String keterangan;
    private String tanggal;
    private String foto;
    private String status;
    private String status_laporan; // nama sesuai field di API, misalnya: "Diterima", "Ditolak", "Diproses"

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

    // --- Setter opsional kalau kamu butuh edit data ---
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
    public String getStatus() { return status; }
}
