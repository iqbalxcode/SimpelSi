package id.polije.simpelsi.Login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.polije.simpelsi.R
import id.polije.simpelsi.fitur.HomeActivity
import id.polije.simpelsi.ui.theme.SimpelSiTheme
import kotlinx.coroutines.delay  // ✅ untuk fungsi delay() di LaunchedEffect

// 🎨 Deklarasi Font
val MontserratExtraBold = FontFamily(
    Font(R.font.montserrat_extrabold, FontWeight.ExtraBold)
)
val MontseratSemiBold = FontFamily(
    Font(R.font.montserrat_semibold, FontWeight.SemiBold)
)

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs: SharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val isLoggedIn: Boolean = prefs.getBoolean("is_logged_in", false)

        window.enterTransition = null
        window.exitTransition = null

        if (isLoggedIn) {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else {
            setContent {
                SimpelSiTheme {
                    Surface(color = Color.White) {
                        ResponsiveWelcomeScreen {
                            startActivity(Intent(this, LoginActivity::class.java))
                            overridePendingTransition(R.anim.slide_in_bottom, R.anim.fade_out)
                            finish()

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResponsiveWelcomeScreen(onMulaiClick: () -> Unit) {
    var showTitle by remember { mutableStateOf(false) }
    var showImage by remember { mutableStateOf(false) }
    var showText by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        showTitle = true
        delay(300)
        showImage = true
        delay(300)
        showText = true
        delay(300)
        showButton = true
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        val screenWidth = this.maxWidth
        val screenHeight = this.maxHeight

        val imageSize = when {
            screenWidth < 360.dp -> 220.dp
            screenWidth < 600.dp -> 300.dp
            else -> 400.dp
        }
        val titleSize = when {
            screenWidth < 360.dp -> 34.sp
            screenWidth < 600.dp -> 42.sp
            else -> 40.sp
        }
        val textSize = when {
            screenWidth < 360.dp -> 16.sp
            screenWidth < 600.dp -> 18.sp
            else -> 18.sp
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(screenHeight * 0.05f))

            // 1️⃣ Judul
            AnimatedVisibility(
                visible = showTitle,
                enter = fadeIn(animationSpec = tween(500)) +
                        slideInVertically(initialOffsetY = { -60 }, animationSpec = tween(500))
            ) {
                Text(
                    text = "Selamat Datang",
                    color = Color(0xFF006400),
                    fontSize = titleSize,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = MontserratExtraBold,
                )
            }

            // 2️⃣ Gambar
            AnimatedVisibility(
                visible = showImage,
                enter = fadeIn(animationSpec = tween(500)) +
                        slideInVertically(initialOffsetY = { 40 }, animationSpec = tween(500))
            ) {
                Image(
                    painter = rememberAsyncImagePainter(R.drawable.welcom),
                    contentDescription = "Ilustrasi DLH",
                    modifier = Modifier.size(imageSize)
                )

            }

            // 3️⃣ Deskripsi
            AnimatedVisibility(
                visible = showText,
                enter = fadeIn(animationSpec = tween(500)) +
                        slideInVertically(initialOffsetY = { 60 }, animationSpec = tween(500))
            ) {
                Text(
                    text = "Bersama kita wujudkan lingkungan yang bersih, hijau, dan berkelanjutan. " +
                            "Mari berpartisipasi aktif dalam menjaga kelestarian alam demi masa depan yang lebih baik.",
                    color = Color(0xFF006400),
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = MontseratSemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            // 4️⃣ Tombol Mulai
            AnimatedVisibility(
                visible = showButton,
                enter = fadeIn(animationSpec = tween(500)) +
                        slideInVertically(initialOffsetY = { 80 }, animationSpec = tween(500))
            ) {
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
                    ) {
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
