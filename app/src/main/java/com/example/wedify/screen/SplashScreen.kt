package com.example.wedify.screen

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.wedify.R
import com.example.wedify.ui.theme.pinkbut
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

@Composable
fun SplashScreen(navController: NavHostController) {
    var stage by remember { mutableStateOf(0) }
    val circleScale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        stage = 1
        circleScale.animateTo(20f, animationSpec = tween(1200, easing = FastOutSlowInEasing))
        stage = 2
        delay(600)
        stage = 3
        delay(1500)
        stage = 4
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (stage < 2) Color(0xFFFF5E94) else Color.White),
        contentAlignment = Alignment.Center
    ) {
        when (stage) {
            1 -> ExpandingDot(scale = circleScale.value)
            3 -> AnimatedScrambledText()
            4 -> SplashContent(navController)
        }
    }
}

@Composable
fun ExpandingDot(scale: Float) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .background(Color.White, shape = CircleShape)
    )
}

@Composable
fun AnimatedScrambledText() {
    val targetWord = "WEDIFY"
    var currentText by remember { mutableStateOf(scramble(targetWord)) }
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        repeat(10) {
            delay(100)
            currentText = scramble(targetWord)
        }
        delay(200)
        currentText = targetWord
        delay(500)
        visible = false
    }

    AnimatedVisibility(visible = visible) {
        Text(
            text = currentText,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF5E94)
        )
    }
}

fun scramble(word: String): String {
    val letters = word.toCharArray()
    letters.shuffle(Random(System.currentTimeMillis()))
    return String(letters)
}

@Composable
fun SplashContent(navController: NavHostController) {
    val user = FirebaseAuth.getInstance().currentUser
    var role by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Ambil data role dari Firestore
    LaunchedEffect(user) {
        if (user == null) {
            navController.navigate("auth") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            val uid = user.uid
            try {
                val doc = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .get()
                    .await() // Coroutine-safe call

                role = doc.getString("role") ?: "user"
                isLoading = false
            } catch (e: Exception) {
                isLoading = false
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
    }

    // Navigasi hanya setelah data berhasil didapat
    LaunchedEffect(role, isLoading) {
        if (!isLoading && role != null) {
            val destination = if (role == "vendor") "vendor-dashboard" else "home"
            navController.navigate(destination) {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    // Tampilan loading
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text("Memuat data pengguna...", color = Color.Gray)
    }
}


@Composable
fun AnimatedLanjutButton(enabled: Boolean = true, onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(150),
        label = "scale"
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = pinkbut),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            }
            .padding(top = 8.dp)
            .height(48.dp)
    ) {
        Text("LANJUT", color = Color.White)
    }
}
