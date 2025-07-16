package com.example.wedify.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.wedify.AppUtil
import com.example.wedify.model.ProductModel
import com.example.wedify.ui.theme.pinkbut
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckOutPage(modifier: Modifier = Modifier, navController: NavHostController, productIds: List<String>) {
    val context = LocalContext.current

    val product = remember { mutableStateOf<ProductModel?>(null) }
    val selectedDate = remember { mutableStateOf("") }
    val selectedTime = remember { mutableStateOf("") }
    val selectedLocationText = remember { mutableStateOf("") }

    val discountPercentage = AppUtil.getDiscountPercentage()
    val taxPercentage = AppUtil.getTaxPercentage()

    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))

    // Ambil ID pertama untuk single booking
    val productId = productIds.firstOrNull() ?: ""

    LaunchedEffect(productId) {
        if (productId.isNotEmpty()) {
            Firebase.firestore.collection("data")
                .document("stok").collection("products")
                .document(productId)
                .get()
                .addOnSuccessListener {
                    val data = it.toObject(ProductModel::class.java)
                    product.value = data
                }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("BOOKING", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFFE91E63))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(Color.White)
            ) {
                product.value?.let { prod ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = prod.images.firstOrNull() ?: "",
                                contentDescription = prod.title,
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(prod.title, fontWeight = FontWeight.SemiBold)
                                Text(
                                    text = "Rp${formatter.format(prod.price.toFloat())}",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    style = LocalTextStyle.current.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
                                )
                                Text("Rp${formatter.format(prod.actualPrice.toFloat())}", fontSize = 14.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = selectedDate.value,
                        onValueChange = { },
                        enabled = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                AppUtil.showDatePicker(context) { date -> selectedDate.value = date }
                            },
                        label = { Text("Tanggal") }
                    )

                    OutlinedTextField(
                        value = selectedTime.value,
                        onValueChange = { },
                        enabled = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                AppUtil.showTimePicker(context) { time -> selectedTime.value = time }
                            },
                        label = { Text("Jam") }
                    )

                    OutlinedTextField(
                        value = selectedLocationText.value,
                        onValueChange = { selectedLocationText.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Lokasi") }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()

                    val subtotal = prod.actualPrice.toFloat()
                    val discount = subtotal * (discountPercentage / 100f)
                    val tax = subtotal * (taxPercentage / 100f)
                    val total = subtotal - discount + tax

                    Spacer(modifier = Modifier.height(16.dp))
                    RowCheckOutItems("Subtotal", formatter.format(subtotal))
                    RowCheckOutItems("Discount (-)", formatter.format(discount))
                    RowCheckOutItems("Tax (+)", formatter.format(tax))
                    RowCheckOutItems("Total", formatter.format(total))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (selectedLocationText.value.isNotEmpty() &&
                                selectedDate.value.isNotEmpty() &&
                                selectedTime.value.isNotEmpty()
                            ) {
                                val bookingData = hashMapOf(
                                    "date" to selectedDate.value,
                                    "time" to selectedTime.value,
                                    "location" to selectedLocationText.value,
                                    "total" to total.toLong(),
                                    "status" to "belum bayar",
                                    "productId" to prod.id,
                                    "productName" to prod.title,
                                    "category" to prod.category
                                )

                                Firebase.firestore.collection("users")
                                    .document(FirebaseAuth.getInstance().currentUser?.uid!!)
                                    .collection("bookings")
                                    .add(bookingData)
                                    .addOnSuccessListener { docRef ->
                                        AppUtil.showToast(context, "Booking berhasil!")
                                        navController.navigate("payment/${docRef.id}")
                                    }
                                    .addOnFailureListener {
                                        AppUtil.showToast(context, "Gagal menyimpan booking")
                                    }
                            } else {
                                AppUtil.showToast(context, "Lengkapi semua data terlebih dahulu")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4081))
                    ) {
                        Text("Booking Sekarang", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } ?: run {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    )
}

@Composable
fun RowCheckOutItems(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Text("Rp$value", fontSize = 18.sp)
    }
}
