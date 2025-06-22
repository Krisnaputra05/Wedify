package com.example.wedify.pages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.wedify.AppUtil
import com.example.wedify.model.ProductModel
import com.example.wedify.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.*

@Composable
fun CheckOutPage(modifier: Modifier = Modifier, navController: NavHostController) {
    val userModel = remember { mutableStateOf(UserModel()) }
    val productList = remember { mutableStateListOf<ProductModel>() }
    val subTotal = remember { mutableStateOf(0f) }
    val discount = remember { mutableStateOf(0f) }
    val tax = remember { mutableStateOf(0f) }
    val total = remember { mutableStateOf(0f) }
    val selectedTime = remember { mutableStateOf("") }
    val selectedDate = remember { mutableStateOf("") }
    val selectedLocationText = remember { mutableStateOf("") }

    val context = LocalContext.current

    fun calculateAndAssign() {
        subTotal.value = 0f
        discount.value = 0f
        tax.value = 0f
        total.value = 0f

        productList.forEach {
            if (it.actualPrice.isNotEmpty()) {
                val quantity = userModel.value.cartItems[it.id] ?: 0
                subTotal.value += it.actualPrice.toFloat() * quantity
            }
        }

        discount.value = subTotal.value * (AppUtil.getDiscountPercentage() / 100f)
        tax.value = subTotal.value * (AppUtil.getTaxPercentage() / 100f)
        total.value = subTotal.value - discount.value + tax.value
    }

    // Fetch user data & product details
    LaunchedEffect(Unit) {
        Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val result = it.result.toObject(UserModel::class.java)
                    if (result != null) {
                        userModel.value = result

                        Firebase.firestore.collection("data")
                            .document("stok").collection("products")
                            .whereIn("id", userModel.value.cartItems.keys.toList())
                            .get().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val resultProducts =
                                        task.result.toObjects(ProductModel::class.java)
                                    productList.clear()
                                    productList.addAll(resultProducts)
                                    calculateAndAssign()
                                }
                            }
                    }
                }
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White)
    ) {
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))

        Text(text = "Checkout", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = if (selectedDate.value.isEmpty()) "Pilih Tanggal" else selectedDate.value,
            onValueChange = { },
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    AppUtil.showDatePicker(context) { date ->
                        selectedDate.value = date
                    }
                },
            label = { Text("Tanggal") }
        )

        OutlinedTextField(
            value = if (selectedTime.value.isEmpty()) "Pilih Jam" else selectedTime.value,
            onValueChange = { },
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    AppUtil.showTimePicker(context) { time ->
                        selectedTime.value = time
                    }
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
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        RowCheckOutItems(title = "Subtotal", value = formatter.format(subTotal.value))
        Spacer(modifier = Modifier.height(8.dp))

        RowCheckOutItems(title = "Discount (-)", value = formatter.format(discount.value))
        Spacer(modifier = Modifier.height(8.dp))

        RowCheckOutItems(title = "Tax(+)", value = formatter.format(tax.value))
        Spacer(modifier = Modifier.height(8.dp))

        RowCheckOutItems(title = "Total", value = formatter.format(total.value))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (selectedLocationText.value.isNotEmpty() &&
                    selectedDate.value.isNotEmpty() &&
                    selectedTime.value.isNotEmpty()
                ) {
                    val product = productList.firstOrNull()
                    val bookingData = hashMapOf(
                        "date" to selectedDate.value,
                        "time" to selectedTime.value,
                        "location" to selectedLocationText.value,
                        "total" to total.value.toLong(),
                        "status" to "belum bayar",
                        "productId" to (product?.id ?: ""),
                        "productName" to (product?.title ?: ""),
                        "category" to (product?.category ?: "")
                    )

                    Firebase.firestore.collection("users")
                        .document(FirebaseAuth.getInstance().currentUser?.uid!!)
                        .collection("bookings")
                        .add(bookingData)
                        .addOnSuccessListener { documentRef ->
                            val bookingId = documentRef.id
                            AppUtil.showToast(context, "Booking berhasil!")
                            navController.navigate("payment/$bookingId")
                        }
                        .addOnFailureListener {
                            AppUtil.showToast(context, "Gagal menyimpan booking")
                        }
                } else {
                    AppUtil.showToast(context, "Lengkapi semua data terlebih dahulu")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Booking Sekarang")
        }
    }
}

@Composable
fun RowCheckOutItems(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Text(text = "Rp$value", fontSize = 18.sp)
    }
}
