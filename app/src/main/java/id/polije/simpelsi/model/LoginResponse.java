package id.polije.simpelsi.model; // ⚠️ Pastikan package ini benar

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    // ❗️ Ini akan memperbaiki error 'getData'
    // Nama 'data' di sini harus cocok dengan key JSON dari PHP
    @SerializedName("data")
    private UserData data; // ❗️ Ini akan memperbaiki error 'UserData'

    // --- Getters ---

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    // ❗️ Ini akan memperbaiki error 'getData'
    public UserData getData() {
        return data;
    }

    // Method bantu
    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }

    /**
     * ❗️ Inner class 'UserData' yang hilang
     * Ini akan memperbaiki error 'UserData'
     */
    public static class UserData {

        // ❗️ Ini akan memperbaiki error 'getId_masyarakat'
        @SerializedName("id_masyarakat")
        private String id_masyarakat;

        // ❗️ Ini akan memperbaiki error 'getNama'
        @SerializedName("nama")
        private String nama;

        // ❗️ Ini akan memperbaiki error 'getEmail'
        @SerializedName("email")
        private String email;

        // --- Getters untuk UserData ---

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