package id.polije.simpelsi.model;

/**
 * Model untuk dikirim ke reset_password.php
 */
public class ResetRequest {
    private String email;
    private String otp;
    private String password;

    public ResetRequest(String email, String otp, String password) {
        this.email = email;
        this.otp = otp;
        this.password = password;
    }
}