package id.polije.simpelsi.fitur;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import id.polije.simpelsi.R;

public class CekStatusLaporanActivity extends Activity {

    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cek_status);

        btnBack = findViewById(R.id.btnBack);
    }

    private void setupButtonBack() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                // kembali ke halaman sebelumnya, misalnya HomeActivity
                Intent intent = new Intent(CekStatusLaporanActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            });
        }
    }


}


