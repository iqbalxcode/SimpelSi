package id.polije.simpelsi.model;

public class LoginRequest {
    String email;
    String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}