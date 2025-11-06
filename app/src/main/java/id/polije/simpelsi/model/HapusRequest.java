package id.polije.simpelsi.model;

// Model JSON untuk dikirim ke hapus_laporan.php
public class HapusRequest {
    String id_laporan;
    String id_masyarakat;

    public HapusRequest(String id_laporan, String id_masyarakat) {
        this.id_laporan = id_laporan;
        this.id_masyarakat = id_masyarakat;
    }
}