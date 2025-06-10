package com.example.wedify.pages

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wedify.AppUtil
import com.example.wedify.model.BookingModel
import com.example.wedify.model.ProductModel
import com.example.wedify.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.android.gms.maps.model.LatLng
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CheckOutPage(modifier: Modifier = Modifier) {

    val userModel = remember { mutableStateOf(UserModel()) }
    val productList = remember { mutableStateListOf(ProductModel()) }
    val subTotal = remember { mutableStateOf(0f) }
    val discount = remember { mutableStateOf(0f) }
    val tax = remember { mutableStateOf(0f) }
    val total = remember { mutableStateOf(0f) }
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }
    val selectedTime = remember { mutableStateOf("") }
    val selectedDate = remember { mutableStateOf("") }
    val selectedLocationText = remember { mutableStateOf("") }
    var showMapPicker by remember { mutableStateOf(false) }

    val context = LocalContext.current

    fun calculateAndAssign() {
        productList.forEach {
            if (it.actualPrice.isNotEmpty()) {
                val quantity = userModel.value.cartItems[it.id] ?: 0
                subTotal.value += it.actualPrice.toFloat() * quantity
            }
        }
        discount.value = subTotal.value * (AppUtil.getDiscountPercentage()) / 100
        tax.value = subTotal.value * (AppUtil.getTaxPercentage() / 100)
        total.value = subTotal.value - discount.value + tax.value
    }

    LaunchedEffect(key1 = Unit) {
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
    ) {
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))

        Text(text = "Checkout", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Tampilan untuk memilih tanggal
        OutlinedTextField(
            value = if (selectedDate.value.isEmpty()) "Pilih Tanggal" else selectedDate.value,
            onValueChange = { },
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    // Misalkan AppUtil.showDatePicker menerima context dan lambda yang mengembalikan tanggal dalam bentuk String
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
        Spacer(modifier = Modifier.height(8.dp))

        Spacer(modifier = Modifier.height(8.dp))

        if (showMapPicker) {
            MapPickerPage(
                onLocationPicked = { latLng, address ->
                    selectedLatLng = latLng
                    selectedLocationText.value = address
                },
                onBack = { showMapPicker = false }
            )
        } else {
            OutlinedTextField(
                value = selectedLocationText.value.ifEmpty { "Pilih Lokasi" },
                onValueChange = {},
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showMapPicker = true },
                label = { Text("Lokasi") }
            )
        }



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
                if (selectedLatLng != null && selectedDate.value.isNotEmpty() && selectedTime.value.isNotEmpty()) {
                    val bookingData = BookingModel(
                        date = selectedDate.value,
                        time = selectedTime.value,
                        location = selectedLocationText.value
                    )

                    Firebase.firestore.collection("users")
                        .document(FirebaseAuth.getInstance().currentUser?.uid!!)
                        .update("booking", bookingData)
                        .addOnSuccessListener {
                            Log.d("CheckOutPage", "Booking berhasil disimpan ke users")
                            AppUtil.showToast(context, "Booking berhasil disimpan!")
                        }
                        .addOnFailureListener {
                            Log.e("CheckOutPage", "Gagal menyimpan booking", it)
                            AppUtil.showToast(context, "Gagal menyimpan booking")
                        }
                } else {
                    Log.w("CheckOutPage", "Tanggal, jam, atau lokasi belum dipilih")
                    AppUtil.showToast(context, "Lengkapi tanggal, jam, dan lokasi dulu")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Booking Sekarang")
        }

    }
}

private fun AppUtil.showLocationPicker(context: Context, function: Any) {}

@Composable
fun RowCheckOutItems(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Text(text = "Rp" + value, fontSize = 18.sp)
    }
}


fun getAddressFromLatLng(context: Context, latLng: LatLng): String {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            addresses[0].getAddressLine(0)
        } else {
            "Alamat tidak ditemukan"
        }
    } catch (e: Exception) {
        "Gagal mendapatkan alamat"
    }
}