package com.example.wedify.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wedify.components.ProductItemView
import com.example.wedify.model.ProductModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import com.example.wedify.model.CategoryModel
import com.example.wedify.GlobalNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryProductPage(
    modifier: Modifier = Modifier,
    categoryId: String
) {
    val productList = remember { mutableStateOf<List<ProductModel>>(emptyList()) }
    val categoryName = remember { mutableStateOf("Kategori") }

    // Ambil nama kategori
    LaunchedEffect(Unit) {
        val catSnap = Firebase.firestore.collection("data")
            .document("stok")
            .collection("categories")
            .document(categoryId)
            .get()
            .await()

        val category = catSnap.toObject(CategoryModel::class.java)
        categoryName.value = category?.nama ?: "Kategori"

        // Ambil produk
        val snapshot = Firebase.firestore.collection("data")
            .document("stok")
            .collection("products")
            .whereEqualTo("category", categoryId)
            .get()
            .await()

        val resultList = snapshot.documents.mapNotNull { doc ->
            doc.toObject(ProductModel::class.java)
        }
        productList.value = resultList
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Set background keseluruhan menjadi putih
    )
    Column {
        // Top App Bar
        TopAppBar(
            title = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = categoryName.value,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        ),
                        color = Color.Black
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = { GlobalNavigation.navController.popBackStack() }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color(0xFFFF4081)
                    )
                }
            },
            actions = {
                // Spacer untuk menyeimbangkan dengan navigationIcon agar teks tetap center
                Spacer(modifier = Modifier.size(48.dp))
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )


        // Daftar Produk 2 kolom
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(productList.value.chunked(2)) { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach {
                        ProductItemView(product = it, modifier = Modifier.weight(1f))
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
