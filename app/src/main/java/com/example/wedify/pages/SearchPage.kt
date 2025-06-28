package com.example.wedify.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.wedify.model.ProductModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(navController: NavController) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var productList by remember { mutableStateOf(listOf<ProductModel>()) }
    var filteredList by remember { mutableStateOf(listOf<ProductModel>()) }
    var showFilterDialog by remember { mutableStateOf(false) }

    // Ambil data dari Firestore
    LaunchedEffect(Unit) {
        Firebase.firestore.collection("data").document("stok").collection("products")
            .get().addOnSuccessListener { snapshot ->
                val data = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(ProductModel::class.java)?.copy(id = doc.id)
                }
                productList = data
                filteredList = data
            }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Input & Filter
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                navController.navigate("home") {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    val query = it.text.trim().lowercase()
                    filteredList = if (query.isEmpty()) {
                        productList
                    } else {
                        productList.filter { p ->
                            p.title.lowercase().contains(query)
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                placeholder = { Text("CARI") },
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )
            IconButton(onClick = { showFilterDialog = true }) {
                Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = Color(0xFFE91E63))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hasil pencarian kosong
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Pencarian Anda Tidak Ditemukan", color = Color.Gray)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredList) { product ->
                    ProductGridItem(product = product) {
                        product.id?.let { id ->
                            navController.navigate("product-details/$id") // ← Ini diperbaiki
                        }
                    }
                }
            }
        }
    }

    // Dialog filter harga
    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Filter") },
            text = {
                Column {
                    Text("Urutkan Berdasarkan Harga", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            filteredList = filteredList.sortedBy {
                                it.actualPrice.toFloatOrNull() ?: 0f
                            }
                            showFilterDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Harga Terendah")
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            filteredList = filteredList.sortedByDescending {
                                it.actualPrice.toFloatOrNull() ?: 0f
                            }
                            showFilterDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Harga Tertinggi")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFilterDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }
}

@Composable
fun ProductGridItem(product: ProductModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(product.images.firstOrNull() ?: ""),
                contentDescription = "Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color.LightGray, RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
