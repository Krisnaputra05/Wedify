package com.example.wedify.screen

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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun SplashScreen(navController: NavHostController) {
    var stage by remember { mutableStateOf(0) }
    val circleScale = remember { Animatable(0f) }

    // Tahapan animasi
    LaunchedEffect(Unit) {
        stage = 1
        circleScale.animateTo(
            targetValue = 20f,
            animationSpec = tween(1200, easing = FastOutSlowInEasing)
        )
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
            2 -> {}
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
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
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
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        showContent = true
    }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Gambar atas
        Image(
            painter = painterResource(id = R.drawable.splash_top),
            contentDescription = "Splash Top",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(800)) + scaleIn()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "WEDIFY",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = pinkbut
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(tween(600, delayMillis = 600)) +
                        slideInVertically(
                            initialOffsetY = { it / 2 },
                            animationSpec = tween(600, easing = FastOutSlowInEasing)
                        ) +
                        scaleIn(initialScale = 0.8f, animationSpec = tween(600))
            ) {
                AnimatedLanjutButton {
                    coroutineScope.launch {
                        delay(300) // Biar animasi selesai
                        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
                        navController.navigate(if (isLoggedIn) "home" else "auth") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                }
            }
        }

        // Gambar bawah
        Image(
            painter = painterResource(id = R.drawable.splash_bottom),
            contentDescription = "Splash Bottom",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
    }
}

@Composable
fun AnimatedLanjutButton(onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(150),
        label = "ButtonScale"
    )

    Button(
        onClick = onClick,
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
