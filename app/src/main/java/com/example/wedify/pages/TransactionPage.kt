package com.example.wedify.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TransactionPage(navController: NavController, modifier: Modifier = Modifier) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val db = Firebase.firestore

    val tabList = listOf("Pembayaran", "Selesai", "Tertunda", "Dibatalkan")
    var selectedTab by remember { mutableStateOf("Pembayaran") }
    var bookings by remember { mutableStateOf(listOf<Map<String, Any>>()) }

    LaunchedEffect(uid) {
        if (uid != null) {
            db.collection("users").document(uid).collection("bookings")
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null && !snapshot.isEmpty) {
                        bookings = snapshot.documents.mapNotNull { it.data?.plus("id" to it.id) }
                    }
                }
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Transaksi",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tabList.forEach { tab ->
                Button(
                    onClick = { selectedTab = tab },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == tab) Color(0xFFE91E63) else Color.White,
                        contentColor = if (selectedTab == tab) Color.White else Color.Black
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(text = tab, fontSize = 14.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        val filtered = bookings.filter {
            val status = (it["status"] ?: "").toString().lowercase()
            when (selectedTab) {
                "Pembayaran" -> status.contains("belum") || status.contains("bayar") || status.contains("pending")
                "Selesai" -> status == "sudah bayar"
                "Tertunda" -> status == "belum bayar"
                "Dibatalkan" -> status == "dibatalkan"
                else -> false
            }
        }

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Tidak ada transaksi.")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filtered) { booking ->
                    TransactionCard(booking, navController)
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun TransactionCard(data: Map<String, Any>, navController: NavController) {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Vendor
            Text(
                text = data["vendor"]?.toString() ?: "Vendor",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            // Nama Produk
            Text(text = data["productName"]?.toString() ?: "Paket Pernikahan")

            // Kategori Produk
            Text(
                text = "Kategori: ${data["category"]?.toString() ?: "-"}",
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            // Tanggal, Jam, Lokasi
            Text(text = "Tanggal: ${data["date"] ?: "-"}", fontSize = 14.sp)
            Text(text = "Jam: ${data["time"] ?: "-"}", fontSize = 14.sp)
            Text(text = "Lokasi: ${data["location"] ?: "-"}", fontSize = 14.sp)

            // Total Harga
            val rawTotal = data["total"]
            val total = when (rawTotal) {
                is Number -> rawTotal.toLong()
                is String -> rawTotal.toLongOrNull() ?: 0L
                else -> 0L
            }
            Text(
                text = "Total: Rp${formatter.format(total)}",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )

            Spacer(Modifier.height(8.dp))

            // Status & Aksi
            val status = (data["status"] ?: "").toString().lowercase()
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                when {
                    status.contains("belum") || status.contains("bayar") || status.contains("pending") -> {
                        Button(onClick = {
                            navController.navigate("payment/${data["id"]}")
                        }) {
                            Text("Bayar Sekarang")
                        }
                    }
                    status == "selesai" -> {
                        Button(onClick = { /* Navigasi ke review */ }) {
                            Text("Nilai Pesanan")
                        }
                    }
                    status == "dibatalkan" -> {
                        Text("Pesanan Dibatalkan", color = Color.Gray)
                    }
                    else -> {
                        Text("Status: ${data["status"]}")
                    }
                }
            }
        }
    }
}
