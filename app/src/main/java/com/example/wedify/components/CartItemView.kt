package com.example.wedify.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.wedify.model.ProductModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.graphics.Color
import com.example.wedify.AppUtil
import androidx.compose.ui.platform.LocalContext
import com.example.wedify.ui.theme.pinkbut


@Composable
fun CartItemView(
    modifier: Modifier = Modifier,
    productId: String,
    quantity: Long
) {
    var product by remember { mutableStateOf(ProductModel()) }
    val context = LocalContext.current

    LaunchedEffect(key1 = productId) {
        Firebase.firestore.collection("data")
            .document("stok")
            .collection("products")
            .document(productId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val data = document.toObject(ProductModel::class.java)
                    if (data != null) {
                        product = data
                    }
                }
            }
    }
    Card(
        modifier = modifier
            .padding(8.dp)
            .border(
                width = 2.dp,
                color = pinkbut, // warna pinggiran pink
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F4F4))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.images.getOrNull(0),
                contentDescription = product.title,
                modifier = Modifier
                    .height(110.dp)
                    .width(110.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = product.title,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$${product.price}",
                        fontSize = 12.sp,
                        style = TextStyle(textDecoration = TextDecoration.LineThrough),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { AppUtil.deleteToCart(context,productId) }) {
                        Text(text = "-", fontSize = 18.sp)
                    }
                    Text(text = "$quantity", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { AppUtil.addToCart(context,productId) }) {
                        Text(text = "+", fontSize = 18.sp)
                    }
                }
            }

            IconButton(
                onClick = {
                    AppUtil.removeFromCart(context, productId)
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus Produk"
                )
            }
        }
    }
}
