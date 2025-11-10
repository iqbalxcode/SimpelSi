package id.polije.simpelsi.model;

import com.google.gson.annotations.SerializedName;

public class ResetRequest {
    @SerializedName("email")
    private String email;

    @SerializedName("otp") // Walaupun tidak dipakai di PHP, bagus untuk keamanan alur
    private String otp;

    @SerializedName("new_password")
    private String new_password;

    public ResetRequest(String email, String otp, String newPassword) {
        this.email = email;
        this.otp = otp;
        this.new_password = newPassword;
    }
}