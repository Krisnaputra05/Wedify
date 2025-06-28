package com.example.wedify.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.wedify.AppUtil
import com.example.wedify.R
import com.example.wedify.ui.theme.pinkbut
import com.example.wedify.ui.theme.poppinsFont
import com.example.wedify.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigate("auth") }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFFFF4081)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Masuk",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontFamily = poppinsFont,
                )
                Spacer(modifier = Modifier.width(48.dp)) // spasi dummy agar rata tengah
            }

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Masukkan alamat email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(50),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = pinkbut,
                    unfocusedBorderColor = pinkbut
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Masukkan kata sandi") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(50),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = pinkbut,
                    unfocusedBorderColor = pinkbut,
                )
            )

            Button(
                onClick = {
                    isLoading = true
                    authViewModel.login(email, password) { success, errorMessage ->
                        if (success) {
                            val uid = FirebaseAuth.getInstance().currentUser?.uid
                            if (uid != null) {
                                FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(uid)
                                    .get()
                                    .addOnSuccessListener { document ->
                                        isLoading = false
                                        val role = document.getString("role") ?: "user"
                                        val destination =
                                            if (role == "vendor") "vendor-dashboard" else "home"
                                        navController.navigate(destination) {
                                            popUpTo("auth") { inclusive = true }
                                        }
                                    }
                                    .addOnFailureListener {
                                        isLoading = false
                                        AppUtil.showToast(context, "Gagal mengambil role.")
                                    }
                            } else {
                                isLoading = false
                                AppUtil.showToast(context, "Gagal mengambil UID.")
                            }
                        } else {
                            isLoading = false
                            AppUtil.showToast(context, errorMessage ?: "Terjadi kesalahan.")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = pinkbut,
                    contentColor = Color.White
                )
            ) {
                Text(text = if (isLoading) "Memproses..." else "Lanjutkan", fontSize = 22.sp)
            }

            Text(
                text = "Lupa kata sandi?",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Belum punya akun? ",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Text(
                text = "Daftar",
                color = Color(0xFFFF4081),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.clickable {
                    navController.navigate("signup")
                }
            )
        }
    }
}
