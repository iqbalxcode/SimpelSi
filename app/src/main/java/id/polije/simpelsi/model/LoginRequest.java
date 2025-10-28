package id.polije.simpelsi.model;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("google_id")
    private String google_id;

    @SerializedName("nama")
    private String nama;

    @SerializedName("email_verified")
    private int email_verified;

    // === CONSTRUCTOR UNTUK LOGIN MANUAL ===
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // === CONSTRUCTOR UNTUK LOGIN GOOGLE ===
    public LoginRequest(String google_id, String email, String nama, boolean email_verified) {
        this.google_id = google_id;
        this.email = email;
        this.nama = nama;
        this.email_verified = email_verified ? 1 : 0;
    }

    // === GETTER (WAJIB UNTUK GSON) ===
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getGoogle_id() { return google_id; }
    public String getNama() { return nama; }
    public int getEmail_verified() { return email_verified; }
}