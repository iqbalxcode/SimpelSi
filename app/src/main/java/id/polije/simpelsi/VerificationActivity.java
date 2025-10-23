package id.polije.simpelsi;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class VerificationActivity extends AppCompatActivity {

    EditText otp1, otp2, otp3, otp4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifikasi);

        // Hubungkan dengan ID di XML
        otp1 = findViewById(R.id.otp_1);
        otp2 = findViewById(R.id.otp_2);
        otp3 = findViewById(R.id.otp_3);
        otp4 = findViewById(R.id.otp_4);

        setupOtpInputs();
    }

    private void setupOtpInputs() {
        // Pindah ke OTP berikutnya otomatis
        otp1.addTextChangedListener(new GenericTextWatcher(otp1, otp2));
        otp2.addTextChangedListener(new GenericTextWatcher(otp2, otp3));
        otp3.addTextChangedListener(new GenericTextWatcher(otp3, otp4));
        otp4.addTextChangedListener(new GenericTextWatcher(otp4, null));

        // Jika hapus (backspace) otomatis ke kolom sebelumnya
        otp2.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL && otp2.getText().toString().isEmpty()) {
                otp1.requestFocus();
                return true;
            }
            return false;
        });

        otp3.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL && otp3.getText().toString().isEmpty()) {
                otp2.requestFocus();
                return true;
            }
            return false;
        });

        otp4.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL && otp4.getText().toString().isEmpty()) {
                otp3.requestFocus();
                return true;
            }
            return false;
        });
    }

    // Kelas bantu untuk pindah otomatis
    private class GenericTextWatcher implements TextWatcher {

        private final EditText currentView;
        private final EditText nextView;

        public GenericTextWatcher(EditText currentView, EditText nextView) {
            this.currentView = currentView;
            this.nextView = nextView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 1 && nextView != null) {
                nextView.requestFocus();
            }
        }
    }
}
