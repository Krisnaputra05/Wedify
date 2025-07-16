package com.example.wedify.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.wedify.GlobalNavigation
import com.example.wedify.model.ProductModel
import com.example.wedify.AppUtil
import com.example.wedify.ui.theme.pinkbut
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun ProductItemView(
    modifier: Modifier = Modifier,
    product: ProductModel
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    fun addToCart() {
        scope.launch {
            try {
                val db = Firebase.firestore
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch

                val userDoc = db.collection("users").document(uid).get().await()
                val cartItems = userDoc.get("cartItems") as? Map<String, Long> ?: emptyMap()
                val updatedCart = cartItems.toMutableMap()
                val currentQty = updatedCart[product.id] ?: 0
                updatedCart[product.id] = currentQty + 1
                db.collection("users").document(uid).update("cartItems", updatedCart).await()
                AppUtil.showToast(context, "Ditambahkan ke keranjang")
            } catch (e: Exception) {
                AppUtil.showToast(context, "Gagal: ${e.message}")
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                GlobalNavigation.navController.navigate("product-details/${product.id}")
            },
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(2.dp, pinkbut),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = product.images.firstOrNull() ?: "",
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Harga coret + icon keranjang
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rp ${product.price}",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    style = TextStyle(textDecoration = TextDecoration.LineThrough)
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { addToCart() }) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Add to cart",
                        tint = Color.Black
                    )
                }
            }

            // Harga asli di bawahnya
            Text(
                text = "Rp ${product.actualPrice}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 6.dp, top = 0.dp)
            )
        }
    }
}

