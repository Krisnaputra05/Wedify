package com.example.wedify.screen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wedify.AppUtil
import com.example.wedify.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun PaymentScreen(
    navController: NavController,
    bookingId: String
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // ✅ Tombol Back ke halaman transaksi
        Button(
            onClick = {
                navController.navigate("transaction") {
                    popUpTo("transaction") { inclusive = true }
                }
            },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Pembayaran", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // ✅ QRIS
        Text("Scan QRIS:", fontWeight = FontWeight.SemiBold)
        Image(
            painter = painterResource(id = R.drawable.qris_image),
            contentDescription = "QRIS",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Info rekening
        Text("Transfer ke Rekening:", fontWeight = FontWeight.SemiBold)
        Text("BANK BCA - 1234567890 a.n. Wedify Wedding Organizer")
        Spacer(modifier = Modifier.height(24.dp))

        // ✅ Tombol Konfirmasi
        Button(
            onClick = {
                confirmPaymentManually(
                    bookingId = bookingId,
                    context = context,
                    navController = navController
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Saya Sudah Transfer")
        }
    }
}

fun confirmPaymentManually(
    bookingId: String,
    context: Context,
    navController: NavController
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    Firebase.firestore.collection("users")
        .document(userId)
        .collection("bookings")
        .document(bookingId)
        .update(
            mapOf("status" to "paid (manual confirmation)")
        )
        .addOnSuccessListener {
            AppUtil.showToast(context, "Pembayaran dikonfirmasi. Admin akan memverifikasi.")
            navController.navigate("transaction") {
                popUpTo("transaction") { inclusive = true }
            }
        }
        .addOnFailureListener {
            AppUtil.showToast(context, "Gagal mengkonfirmasi pembayaran.")
        }
}
