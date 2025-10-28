package id.polije.simpelsi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model untuk diterima dari reset_password.php
 */
public class ResetResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    // Method bantu untuk cek status
    public boolean isSuccess() {
        return status != null && status.equals("success");
    }
}