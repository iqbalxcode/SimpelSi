    package id.polije.simpelsi.CekStatusLaporan;
    import java.io.Serializable;
    import com.google.gson.annotations.SerializedName;

    public class Laporan implements Serializable {

        @SerializedName("created_at") // ❗️ Tambahkan ini
        private String created_at;
        @SerializedName("id_laporan")
        private String idLaporan;

        @SerializedName("id_masyarakat")
        private String idMasyarakat;

        @SerializedName("nama")
        private String nama;

        @SerializedName("lokasi")
        private String lokasi;

        @SerializedName("keterangan")
        private String keterangan;

        @SerializedName("tanggal")
        private String tanggal;

        // 🔹 API bisa kirim "foto" atau "foto_url"
        @SerializedName("foto")
        private String foto;

        @SerializedName("foto_url")
        private String fotoUrl;

        @SerializedName("status_laporan")
        private String statusLaporan;

        // --- Getter ---
        public String getIdLaporan() {
            return idLaporan;
        }
        public String getCreated_at() {
            return created_at;
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

        /**
         * 🔹 Mengembalikan URL lengkap foto:
         *  1️⃣ Jika `foto_url` sudah lengkap → gunakan itu.
         *  2️⃣ Jika hanya nama file (foto) → buatkan URL lengkapnya.
         */
        public String getFoto() {
            if (fotoUrl != null && !fotoUrl.trim().isEmpty()) {
                return fotoUrl;
            } else if (foto != null && !foto.trim().isEmpty()) {
                return "https://simpelsi.medianewsonline.com/api/uploads/" + foto;
            } else {
                return null;
            }
        }

        public String getStatusLaporan() {
            return statusLaporan != null ? statusLaporan : "Diproses";
        }

        // --- Setter ---
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

        public void setFoto(String foto) {
            this.foto = foto;
        }

        public void setFotoUrl(String fotoUrl) {
            this.fotoUrl = fotoUrl;
        }

        public void setStatusLaporan(String statusLaporan) {
            this.statusLaporan = statusLaporan;
        }
    }
