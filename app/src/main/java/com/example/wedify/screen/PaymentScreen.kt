package com.example.wedify.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.wedify.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*

@Composable
fun PaymentScreen(
    navController: NavController,
    bookingId: String
) {
    val context = LocalContext.current
    var proofUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val db = Firebase.firestore
    val storage = Firebase.storage

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            proofUri = uri
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.align(Alignment.Start),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Black
            )
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Pembayaran", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Scan QRIS:", fontWeight = FontWeight.SemiBold)
        Image(
            painter = painterResource(id = R.drawable.qris_image),
            contentDescription = "QRIS",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .border(1.dp, Color.Gray)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Transfer ke Rekening:", fontWeight = FontWeight.SemiBold)
        Text("BANK BCA - 1234567890 a.n. Wedify Wedding Organizer")
        Spacer(modifier = Modifier.height(24.dp))

        proofUri?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Bukti Transfer",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (proofUri == null) {
                    launcher.launch("image/*")
                } else if (!isUploading && userId != null) {
                    isUploading = true
                    val filename = "proof_${UUID.randomUUID()}.jpg"
                    val ref = storage.reference.child("proofs/$userId/$filename")

                    ref.putFile(proofUri!!)
                        .addOnSuccessListener {
                            ref.downloadUrl.addOnSuccessListener { uri ->
                                db.collection("users").document(userId)
                                    .collection("bookings").document(bookingId)
                                    .update(
                                        mapOf(
                                            "buktiTransferUrl" to uri.toString(),
                                            "status" to "menunggu konfirmasi admin"
                                        )
                                    )
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Bukti berhasil di-upload & status diperbarui", Toast.LENGTH_SHORT).show()
                                        navController.navigate("home/2") {
                                            popUpTo("home") { inclusive = false }
                                        }
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "Gagal menyimpan ke database", Toast.LENGTH_SHORT).show()
                                        isUploading = false
                                    }
                            }.addOnFailureListener {
                                Toast.makeText(context, "Gagal mengambil URL", Toast.LENGTH_SHORT).show()
                                isUploading = false
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Upload gagal", Toast.LENGTH_SHORT).show()
                            isUploading = false
                        }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isUploading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE91E63), // Warna pink Wedify
                contentColor = Color.White
            )
        ) {
            Text(
                when {
                    proofUri == null -> "Upload Bukti Transfer"
                    isUploading -> "Mengunggah..."
                    else -> "Selesai & Konfirmasi"
                }
            )
        }
    }
}
