package id.polije.simpelsi.model;

/**
 * Model untuk dikirim ke request_otp.php
 */
public class OtpRequest {
    private String email;

    public OtpRequest(String email) {
        this.email = email;
    }
}