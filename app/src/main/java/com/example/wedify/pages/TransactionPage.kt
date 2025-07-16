package com.example.wedify.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wedify.ui.theme.pinkbut
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionPage(navController: NavController, modifier: Modifier = Modifier) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val db = Firebase.firestore

    val tabList = listOf("Semua", "Selesai", "Tertunda", "Dibatalkan")
    var selectedTab by remember { mutableStateOf("Semua") }
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
    Column(modifier = modifier
        .fillMaxSize()
        .padding(12.dp)) {

        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Transaksi",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.Black
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.White
            )
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
                "Semua" -> true
                "Selesai" -> status == "sudah bayar"
                "Tertunda" -> status == "belum bayar" || status == "menunggu konfirmasi admin"
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
                    TransactionCard(booking, navController, selectedTab)
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun TransactionCard(data: Map<String, Any>, navController: NavController, currentTab: String) {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    val db = Firebase.firestore
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFEF4F4F4),
            contentColor = Color.Black
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = data["vendor"]?.toString() ?: "Vendor",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Text(text = data["productName"]?.toString() ?: "Paket Pernikahan")

            Text(
                text = "Kategori: ${data["category"]?.toString() ?: "-"}",
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Text(text = "Tanggal: ${data["date"] ?: "-"}", fontSize = 14.sp)
            Text(text = "Jam: ${data["time"] ?: "-"}", fontSize = 14.sp)
            Text(text = "Lokasi: ${data["location"] ?: "-"}", fontSize = 14.sp)

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

            val status = (data["status"] ?: "").toString().lowercase()

            Column(modifier = Modifier.fillMaxWidth()) {
                if (currentTab == "Tertunda") {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Button(onClick = {
                            navController.navigate("payment/${data["id"]}")
                        },colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE91E63), // warna background button
                            contentColor = Color.White // warna teks
                        ),
                            border = BorderStroke(1.dp, Color(0xFFFF5C8D))
                            ) {
                            Text("Bayar Sekarang")
                        }

                        if (status == "belum bayar" || status.contains("pending")) {
                            Button(onClick = {
                                if (userId != null && data["id"] != null) {
                                    db.collection("users")
                                        .document(userId)
                                        .collection("bookings")
                                        .document(data["id"].toString())
                                        .update("status", "dibatalkan")
                                }
                            },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE91E63), // warna background button
                                    contentColor = Color.White // warna teks
                                ),
                                border = BorderStroke(1.dp, Color(0xFFFF5C8D)))
                            {
                                Text("Ajukan Pembatalan")
                            }
                        }
                    }
                } else {
                    Text("Status: ${data["status"]}")

                    if ((currentTab == "Semua") && (status == "belum bayar" || status.contains("pending"))) {
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = {
                                    navController.navigate("payment/${data["id"]}")
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE91E63),
                                    contentColor = Color.White
                                ),
                                border = BorderStroke(1.dp, Color(0xFFFF5C8D))
                            ) {
                                Text("Bayar Sekarang")
                            }
                            Button(
                                onClick = {
                                    if (userId != null && data["id"] != null) {
                                        db.collection("users")
                                            .document(userId)
                                            .collection("bookings")
                                            .document(data["id"].toString())
                                            .update("status", "dibatalkan")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE91E63),
                                    contentColor = Color.White
                                ),
                                border = BorderStroke(1.dp, Color(0xFFFF5C8D))
                            ) {
                                Text("Ajukan Pembatalan")
                            }
                        }
                    }
                }
            }
        }
    }
}
