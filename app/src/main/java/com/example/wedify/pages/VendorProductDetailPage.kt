package com.example.wedify.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.wedify.model.ProductModel
import com.example.wedify.ui.theme.pinkbut
import com.google.firebase.firestore.FirebaseFirestore
import com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import com.tbuonomo.viewpagerdotsindicator.compose.type.ShiftIndicatorType

@Composable
fun VendorProductDetailPage(navController: NavController, productId: String) {
    var product by remember { mutableStateOf(ProductModel()) }

    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance()
            .collection("data").document("stok")
            .collection("products").document(productId)
            .get()
            .addOnSuccessListener {
                val fetched = it.toObject(ProductModel::class.java)
                if (fetched != null) product = fetched
            }
    }

    val pagerState = rememberPagerState(pageCount = { product.images.size })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = product.title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalPager(state = pagerState) { page ->
            AsyncImage(
                model = product.images[page],
                contentDescription = "Product Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp)),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (product.images.isNotEmpty()) {
            DotsIndicator(
                dotCount = product.images.size,
                pagerState = pagerState,
                type = ShiftIndicatorType(
                    dotsGraphic = DotGraphic(color = pinkbut, size = 6.dp)
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Kategori: ${product.category}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Text(
                text = "Rp${product.price}",
                style = TextStyle(
                    fontSize = 16.sp,
                    textDecoration = TextDecoration.LineThrough
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Rp${product.actualPrice}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = pinkbut
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Divider(thickness = 1.dp)

        Text(
            text = "Product Description :",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Text(
            text = product.description,
            textAlign = TextAlign.Justify
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (product.otherDetails.isNotEmpty()) {
            Text(
                text = "Other Details:",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                product.otherDetails.forEach { (key, value) ->
                    if (!key.equals("vendorId", ignoreCase = true)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = key, fontWeight = FontWeight.Medium)
                            Text(text = value)
                        }
                    }
                }
            }
        }
    }
}
