package com.example.wedify.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.wedify.model.ProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductPage(navController: NavController, productId: String? = null) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var actualPrice by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var newImageUrl by remember { mutableStateOf("") }
    var imageUrls by remember { mutableStateOf(mutableListOf<String>()) }
    var isUploading by remember { mutableStateOf(false) }

    var newDetailKey by remember { mutableStateOf("") }
    var newDetailValue by remember { mutableStateOf("") }
    var otherDetails by remember { mutableStateOf(mutableMapOf<String, String>()) }

    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Load data jika edit
    LaunchedEffect(productId) {
        if (productId != null) {
            val doc = FirebaseFirestore.getInstance()
                .collection("data")
                .document("stok")
                .collection("products")
                .document(productId)
                .get()
                .await()

            if (doc.exists()) {
                val data = doc.toObject(ProductModel::class.java)
                data?.let {
                    title = it.title ?: ""
                    description = it.description ?: ""
                    price = it.price ?: ""
                    actualPrice = it.actualPrice ?: ""
                    category = it.category ?: ""
                    location = it.location ?: ""
                    imageUrls = it.images.toMutableList()
                    otherDetails = it.otherDetails.toMutableMap()
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Row tombol back + title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali"
                    )
                }
                Text(
                    if (productId != null) "Edit Produk" else "Tambah Produk",
                    fontSize = 24.sp,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Harga") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            OutlinedTextField(value = actualPrice, onValueChange = { actualPrice = it }, label = { Text("Harga Asli") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Kategori") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Lokasi") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))

            Spacer(modifier = Modifier.height(16.dp))

            Text("Tambahkan URL Gambar")
            OutlinedTextField(value = newImageUrl, onValueChange = { newImageUrl = it }, label = { Text("URL Gambar") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = {
                if (newImageUrl.isNotBlank()) {
                    imageUrls = imageUrls.toMutableList().apply { add(newImageUrl.trim()) }
                    newImageUrl = ""
                }
            }, modifier = Modifier.padding(top = 8.dp)) {
                Text("Tambah Gambar")
            }

            Column {
                imageUrls.forEach { url ->
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = rememberAsyncImagePainter(url),
                            contentDescription = null,
                            modifier = Modifier
                                .weight(1f)
                                .height(180.dp)
                                .padding(vertical = 4.dp),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(onClick = {
                            imageUrls = imageUrls.toMutableList().apply { remove(url) }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus Gambar")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Divider()
            Text("Other Details", fontSize = 20.sp, modifier = Modifier.padding(top = 16.dp))

            OutlinedTextField(
                value = newDetailKey,
                onValueChange = { newDetailKey = it },
                label = { Text("Judul Detail (Contoh: Durasi Acara)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = newDetailValue,
                onValueChange = { newDetailValue = it },
                label = { Text("Isi Detail (Contoh: Maks. 6 jam)") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            Button(
                onClick = {
                    if (newDetailKey.isNotBlank() && newDetailValue.isNotBlank()) {
                        otherDetails = otherDetails.toMutableMap().apply {
                            put(newDetailKey.trim(), newDetailValue.trim())
                        }
                        newDetailKey = ""
                        newDetailValue = ""
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Tambah Other Detail")
            }

            Spacer(modifier = Modifier.height(8.dp))
            otherDetails.forEach { (key, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("$key: $value", modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        otherDetails = otherDetails.toMutableMap().apply {
                            remove(key)
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (title.isNotEmpty()) {
                        isUploading = true
                        val productIdToUse = productId ?: UUID.randomUUID().toString()

                        val product = ProductModel(
                            id = productIdToUse,
                            title = title,
                            description = description,
                            price = price,
                            actualPrice = actualPrice,
                            category = category,
                            location = location,
                            images = imageUrls,
                            otherDetails = otherDetails.toMap(),
                            vendorId = uid
                        )

                        FirebaseFirestore.getInstance()
                            .collection("data")
                            .document("stok")
                            .collection("products")
                            .document(productIdToUse)
                            .set(product)
                            .addOnSuccessListener {
                                navController.popBackStack()
                            }

                        isUploading = false
                    }
                },
                enabled = !isUploading
            ) {
                Text(if (isUploading) "Mengupload..." else "Simpan")
            }
        }
    }
}
