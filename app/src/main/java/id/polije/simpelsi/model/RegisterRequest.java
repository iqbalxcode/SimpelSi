package id.polije.simpelsi.model;

public class RegisterRequest {
    String nama;
    String email;
    String password;

    public RegisterRequest(String nama, String email, String password) {
        this.nama = nama;
        this.email = email;
        this.password = password;
    }
}