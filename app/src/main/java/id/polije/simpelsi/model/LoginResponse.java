package id.polije.simpelsi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model respon login dari server.
 * Menyesuaikan dengan struktur JSON dari PHP:
 * {
 *   "status": "success",
 *   "message": "Login berhasil",
 *   "data": {
 *       "id_masyarakat": "123",
 *       "nama": "Dimas Aldi",
 *       "email": "dimas@example.com"
 *   }
 * }
 */
public class LoginResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    // Objek data yang berisi informasi pengguna
    @SerializedName("data")
    private UserData data;

    // --- Getter utama ---
    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public UserData getData() {
        return data;
    }

    // --- Logika bantu ---
    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }

    /**
     * Kelas internal yang mewakili data pengguna
     */
    public static class UserData {

        @SerializedName("id_masyarakat")
        private String id_masyarakat;

        @SerializedName("nama")
        private String nama;

        @SerializedName("email")
        private String email;

        // --- Getters ---
        public String getId_masyarakat() {
            return id_masyarakat;
        }

        public String getNama() {
            return nama;
        }

        public String getEmail() {
            return email;
        }
    }
}
