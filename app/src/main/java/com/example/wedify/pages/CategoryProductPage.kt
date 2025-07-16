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

    // Fetch kategori dan produk dari Firestore
    LaunchedEffect(Unit) {
        val catSnap = Firebase.firestore.collection("data")
            .document("stok")
            .collection("categories")
            .document(categoryId)
            .get()
            .await()

        val category = catSnap.toObject(CategoryModel::class.java)
        categoryName.value = category?.nama ?: "Kategori"

        val snapshot = Firebase.firestore.collection("data")
            .document("stok")
            .collection("products")
            .whereEqualTo("category", categoryId)
            .get()
            .await()

        productList.value = snapshot.documents.mapNotNull {
            it.toObject(ProductModel::class.java)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        TopAppBar(
            title = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = categoryName.value,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = { GlobalNavigation.navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFFFF4081))
                }
            },
            actions = { Spacer(modifier = Modifier.size(48.dp)) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(productList.value.chunked(2)) { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { product ->
                        ProductItemView(product = product, modifier = Modifier.weight(1f))
                    }
                    if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

