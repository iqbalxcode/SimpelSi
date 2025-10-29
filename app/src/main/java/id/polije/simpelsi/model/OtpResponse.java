package id.polije.simpelsi.model;

import com.google.gson.annotations.SerializedName;

public class OtpResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    // ❗️⬇️ TAMBAHKAN INI ⬇️
    @SerializedName("otp")
    private String otp;
    // ❗️⬆️ TAMBAHKAN INI ⬆️

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    // ❗️⬇️ TAMBAHKAN GETTER INI ⬇️
    public String getOtp() {
        return otp;
    }
    // ❗️⬆️ TAMBAHKAN GETTER INI ⬆️

    public boolean isSuccess() {
        return status != null && status.equals("success");
    }
}