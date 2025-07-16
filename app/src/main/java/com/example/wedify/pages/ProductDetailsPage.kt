package com.example.wedify.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.wedify.AppUtil
import com.example.wedify.GlobalNavigation.navController
import com.example.wedify.components.HorizontalLine
import com.example.wedify.components.NavActionBar
import com.example.wedify.model.ProductModel
import com.example.wedify.ui.theme.pinkbut
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import com.tbuonomo.viewpagerdotsindicator.compose.type.ShiftIndicatorType
import kotlinx.coroutines.delay

@Composable
fun ProductDetailsPage(modifier: Modifier = Modifier, productId: String) {
    var product by remember { mutableStateOf(ProductModel()) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Firebase.firestore.collection("data").document("stok")
            .collection("products")
            .document(productId).get()
            .addOnSuccessListener {
                it.toObject(ProductModel::class.java)?.let { fetched ->
                    product = fetched
                }
            }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp) // ruang untuk NavActionBar
        ) {
            // AppBar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = pinkbut
                    )
                }

                Text(
                    text = product.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val pagerState = rememberPagerState(pageCount = { product.images.size })

            HorizontalPager(state = pagerState) { page ->
                AsyncImage(
                    model = product.images[page],
                    contentDescription = "Product image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                )
            }

            LaunchedEffect(product.images) {
                while (true) {
                    delay(3000)
                    val next = (pagerState.currentPage + 1) % (product.images.size.takeIf { it > 0 } ?: 1)
                    pagerState.animateScrollToPage(next)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (product.images.isNotEmpty()) {
                DotsIndicator(
                    dotCount = product.images.size,
                    type = ShiftIndicatorType(
                        dotsGraphic = DotGraphic(
                            color = pinkbut,
                            size = 6.dp
                        )
                    ),
                    pagerState = pagerState
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rp ${product.price}",
                    fontSize = 16.sp,
                    style = TextStyle(textDecoration = TextDecoration.LineThrough)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Rp ${product.actualPrice}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /* TODO: Tambah ke favorite */ }) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite")
                }
            }

            HorizontalLine(color = pinkbut, thickness = 4.dp)

            Text(
                text = "Deskripsi Produk :",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.description,
                fontSize = 16.sp,
                textAlign = TextAlign.Justify,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalLine(color = pinkbut, thickness = 4.dp)

            Text(
                text = "Detail Lainnya:",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                product.otherDetails.forEach { (key, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = key,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = value,
                            fontSize = 14.sp,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        NavActionBar(
            onCartClick = {
                AppUtil.addToCart(context, productId)
            },
            onOrderClick = {
                navController.navigate("checkout/$productId")
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

    }
}
