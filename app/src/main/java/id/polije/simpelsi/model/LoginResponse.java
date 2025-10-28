package id.polije.simpelsi.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    // ✅ ubah ini
    @SerializedName("data")  // <-- sesuaikan dengan field JSON dari PHP
    private User user;

    public String getStatus() {
        return status;
    }

    public boolean isSuccess() {
        return "success".equals(status);
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

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
