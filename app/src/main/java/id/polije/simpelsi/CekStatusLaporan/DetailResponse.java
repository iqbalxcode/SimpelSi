package id.polije.simpelsi.CekStatusLaporan;

import com.google.gson.annotations.SerializedName;

public class DetailResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private DataLaporan data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public DataLaporan getData() {
        return data;
    }

    public static class DataLaporan {
        @SerializedName("id_laporan")
        private int idLaporan;

        @SerializedName("nama")
        private String nama;

        @SerializedName("lokasi")
        private String lokasi;

        @SerializedName("keterangan")
        private String keterangan;

        @SerializedName("tanggal")
        private String tanggal;

        @SerializedName("status")
        private String status;

        @SerializedName("foto")
        private String foto;

        @SerializedName("balasan")
        private String balasan;

        public int getIdLaporan() { return idLaporan; }
        public String getNama() { return nama; }
        public String getLokasi() { return lokasi; }
        public String getKeterangan() { return keterangan; }
        public String getTanggal() { return tanggal; }
        public String getStatus() { return status; }
        public String getFoto() { return foto; }
        public String getBalasan() { return balasan; }
    }
}
