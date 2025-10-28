package id.polije.simpelsi.model;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {

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

    public boolean isSuccess() {
        return status != null && status.equals("success");
    }
}