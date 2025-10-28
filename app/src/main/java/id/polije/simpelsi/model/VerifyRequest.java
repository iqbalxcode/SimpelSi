package id.polije.simpelsi.model;

/**
 * Model untuk dikirim ke verify_otp.php
 */
public class VerifyRequest {
    private String email;
    private String otp;

    public VerifyRequest(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }
}