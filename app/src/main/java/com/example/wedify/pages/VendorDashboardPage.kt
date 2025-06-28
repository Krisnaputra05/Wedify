package com.example.wedify.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

@Composable
fun VendorDashboardPage(navController: NavController) {
    val viewModel: ProductViewModel = viewModel()
    val products by viewModel.products.collectAsState()

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
                    popUpTo(0) // clear semua backstack
                }
            }) {
                Icon(Icons.Default.Logout, contentDescription = "Logout")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate("add-edit-page")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tambah Produk Baru")
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
fun ProductCard(product: ProductModel, viewModel: ProductViewModel, navController: NavController) {
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
                    }
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
            Button(
                onClick = { navController.navigate("verifikasi-booking") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Verifikasi Pembayaran")
            }
        }
    }
}
