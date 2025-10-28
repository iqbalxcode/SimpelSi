package id.polije.simpelsi.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("user")
    private User user;

    // ✅ TAMBAHKAN getStatus() — INI YANG BIKIN MERAH!
    public String getStatus() {
        return status;
    }

    // ✅ isSuccess() — BOLEH DIPAKAI (lebih aman dari null)
    public boolean isSuccess() {
        return "success".equals(status);
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    // ✅ Inner class User — SUDAH BENAR
    public static class User {
        @SerializedName("id_masyarakat")
        private String id;

        @SerializedName("nama")
        private String nama;

        @SerializedName("email")
        private String email;

        public String getId() { return id; }
        public String getNama() { return nama; }
        public String getEmail() { return email; }
    }
}