package id.polije.simpelsi.Login

import android.content.Intent
import android.content.SharedPreferences // ❗️ Import SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import id.polije.simpelsi.R
import id.polije.simpelsi.fitur.HomeActivity // ❗️ Import HomeActivity Anda
import id.polije.simpelsi.ui.theme.SimpelSiTheme

// 🎨 Deklarasi Font (Sudah benar)
val MontserratExtraBold = FontFamily(
    Font(R.font.montserrat_extrabold, FontWeight.ExtraBold)
)
val MontseratSemiBold = FontFamily(
    Font(R.font.montserrat_semibold, FontWeight.SemiBold)
)

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- ⬇️ PERBAIKAN LOGIKA SESI DI SINI ⬇️ ---

        // 1. Cek SharedPreferences (nama harus sama dengan di LoginActivity)
        val prefs: SharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)

        // 2. Cek key "is_logged_in"
        val isLoggedIn: Boolean = prefs.getBoolean("is_logged_in", false)

        if (isLoggedIn) {
            // 3. JIKA SUDAH LOGIN: Langsung ke HomeActivity
            // (Melewati layar Welcome ini seluruhnya)
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Tutup WelcomeActivity agar tidak bisa "back" ke sini
        } else {
            // 4. JIKA BELUM LOGIN: Tampilkan layar Welcome
            setContent {
                SimpelSiTheme {
                    Surface(color = Color.White) {
                        ResponsiveWelcomeScreen {
                            // Saat tombol "Mulai" ditekan, pindah ke LoginActivity
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish() // Tutup WelcomeActivity
                        }
                    }
                }
            }
        }
        // --- ⬆️ AKHIR PERBAIKAN ⬆️ ---
    }
}

// ❗️ Kode Composable Anda di bawah ini tidak perlu diubah.
// (ResponsiveWelcomeScreen dan WelcomeScreenPreview sudah benar)

@Composable
fun ResponsiveWelcomeScreen(onMulaiClick: () -> Unit) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        val screenWidth = this.maxWidth
        val screenHeight = this.maxHeight

        // --- Variabel Responsif ---
        val imageSize = when {
            screenWidth < 360.dp -> 220.dp
            screenWidth < 600.dp -> 300.dp
            else -> 400.dp
        }
        val titleSize = when {
            screenWidth < 360.dp -> 30.sp
            screenWidth < 600.dp -> 38.sp
            else -> 40.sp
        }
        val textSize = when {
            screenWidth < 360.dp -> 14.sp
            screenWidth < 600.dp -> 16.sp
            else -> 16.sp
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(screenHeight * 0.05f))

            Text(
                text = "Selamat Datang",
                color = Color(0xFF006400),
                fontSize = titleSize,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = MontserratExtraBold,
            )

            Image(
                painter = painterResource(id = R.drawable.welcom),
                contentDescription = "Ilustrasi DLH",
                modifier = Modifier
                    .size(imageSize)
                    .padding(top = 0.dp)
            )

            Text(
                text = "Bersama kita wujudkan lingkungan yang bersih, hijau, dan berkelanjutan. " +
                        "Mari berpartisipasi aktif dalam menjaga kelestarian alam demi masa depan yang lebih baik.",
                color = Color(0xFF006400),
                fontWeight = FontWeight.SemiBold,
                fontFamily = MontseratSemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Button(
                onClick = onMulaiClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006400)),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Spacer(modifier = Modifier.weight(0.5f))
                    Text(
                        text = "Mulai",
                        color = Color.White,
                        fontSize = textSize,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = MontseratSemiBold
                    )
                    Spacer(modifier = Modifier.weight(0.5f))
                    Icon(
                        painter = painterResource(id = R.drawable.next),
                        contentDescription = "Mulai",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(screenHeight * 0.05f))
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    SimpelSiTheme {
        ResponsiveWelcomeScreen(onMulaiClick = {})
    }
}