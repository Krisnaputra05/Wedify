package com.example.wedify.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.wedify.model.ProductModel
import com.example.wedify.viewmodel.ProductViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.tasks.Tasks
import com.example.wedify.ui.theme.pinkbut

@Composable
fun VendorDashboardPage(navController: NavController) {
    val viewModel: ProductViewModel = viewModel()
    val products by viewModel.products.collectAsState()

    var jumlahBelumVerifikasi by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        fetchJumlahBelumVerifikasi { jumlah ->
            jumlahBelumVerifikasi = jumlah
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Dashboard Vendor", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("login") {
                    popUpTo(0)
                }
            }) {
                Icon(Icons.Default.Logout, contentDescription = "Logout")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol horizontal
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp), // Pastikan tinggi sama
            horizontalArrangement = Arrangement.spacedBy(8.dp) // Spasi di antara tombol
        ) {
            Button(
                onClick = { navController.navigate("add-edit-page") },
                modifier = Modifier
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = pinkbut),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Tambah Produk Baru",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Button(
                onClick = { navController.navigate("verifikasi-booking") },
                modifier = Modifier
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Verifikasi Transaksi",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            Text("Memuat jumlah pembayaran belum diverifikasi...", fontSize = 14.sp, color = Color.Black)
        } else {
            Text(
                "Jumlah pembayaran belum diverifikasi: $jumlahBelumVerifikasi",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (products.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Belum ada produk.")
            }
        } else {
            LazyColumn {
                items(products) { product ->
                    ProductCard(product, viewModel, navController)
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: ProductModel,
    viewModel: ProductViewModel,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                navController.navigate("vendor-product-detail/${product.id}")
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(product.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(4.dp))
            Text("Kategori: ${product.category}")
            Text("Harga: Rp${product.price}")
            Text("Deskripsi: ${product.description}")
            Text("Jumlah Gambar: ${product.images.size}")

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        navController.navigate("add-edit-page/${product.id}")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                ) {
                    Text("Edit")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { viewModel.deleteProduct(product.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Hapus")
                }
            }
        }
    }
}

fun fetchJumlahBelumVerifikasi(onResult: (Int) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()
    firestore.collection("users")
        .get()
        .addOnSuccessListener { usersSnapshot ->
            var total = 0
            val tasks = mutableListOf<com.google.android.gms.tasks.Task<*>>()

            for (userDoc in usersSnapshot.documents) {
                val userId = userDoc.id
                val task = firestore.collection("users")
                    .document(userId)
                    .collection("bookings")
                    .whereEqualTo("status", "belum bayar")
                    .get()
                    .addOnSuccessListener { bookingSnap ->
                        total += bookingSnap.size()
                    }
                tasks.add(task)
            }

            Tasks.whenAllComplete(tasks)
                .addOnSuccessListener {
                    onResult(total)
                }
        }
}
