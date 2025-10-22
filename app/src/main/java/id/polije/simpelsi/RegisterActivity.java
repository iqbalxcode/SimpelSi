package id.polije.simpelsi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class RegisterActivity extends AppCompatActivity {

    Button btnKirim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnKirim = findViewById(R.id.btn_kirim_register);

        // Tambahkan logika untuk tombol kirim (validasi, lalu kirim ke server)
        // ...
    }
}