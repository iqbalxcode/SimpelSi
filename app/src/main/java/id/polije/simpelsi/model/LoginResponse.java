package id.polije.simpelsi.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private UserData data; // Ini akan null jika login gagal

    // Getter
    public boolean isSuccess() {
        return status != null && status.equals("success");
    }
    public String getMessage() {
        return message;
    }
    public UserData getData() {
        return data;
    }

    // Class untuk data pengguna di dalam JSON
    public static class UserData {
        @SerializedName("id_masyarakat") // atau id_masyarakat
        private String id;

        @SerializedName("nama")
        private String nama;

        @SerializedName("email")
        private String email;

        // Getter
        public String getId() { return id; }
        public String getNama() { return nama; }
        public String getEmail() { return email; }
    }
}