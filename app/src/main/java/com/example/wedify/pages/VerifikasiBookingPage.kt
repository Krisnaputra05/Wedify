package com.example.wedify.pages

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.wedify.model.BookingModel
import com.example.wedify.ui.theme.pinkbut
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifikasiBookingPage(navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    var bookings by remember { mutableStateOf<List<BookingModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        fetchBookings(firestore) { result ->
            bookings = result
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verifikasi Transaksi") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = pinkbut
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                bookings.isEmpty() -> {
                    Text(
                        text = "Tidak Ada Verifikasi",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        items(bookings) { booking ->
                            BookingItem(
                                booking = booking,
                                onVerifyClick = {
                                    updateStatusToPaid(firestore, booking)
                                    bookings = bookings.map {
                                        if (it.id == booking.id) it.copy(status = "sudah bayar") else it
                                    }
                                },
                                onInvalidClick = {
                                    updateStatusToInvalid(firestore, booking)
                                    bookings = bookings.map {
                                        if (it.id == booking.id) it.copy(status = "tidak valid") else it
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookingItem(
    booking: BookingModel,
    onVerifyClick: () -> Unit,
    onInvalidClick: () -> Unit
) {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    var isImageVisible by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Nama User: ${booking.name ?: "-"}")
            Text("Produk: ${booking.productName}")
            Text("Tanggal: ${booking.date.ifBlank { "-" }} ${booking.time.ifBlank { "-" }}")
            Text("Lokasi: ${booking.location.ifBlank { "-" }}")
            Text("Total: Rp${formatter.format(booking.total)}")
            Text("Status: ${booking.status}")
            Text("Bukti Transfer:")

            // Tombol untuk menampilkan gambar bukti transfer
            booking.buktiTransferUrl?.takeIf { it.isNotEmpty() }?.let { url ->
                // Tombol toggle
                Button(
                    onClick = { isImageVisible = !isImageVisible },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = pinkbut)
                ) {
                    Text(if (isImageVisible) "Sembunyikan Bukti Transfer" else "Lihat Bukti Transfer")
                }

                if (isImageVisible) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = rememberAsyncImagePainter(url),
                        contentDescription = "Bukti Transfer",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }

            if (booking.status == "menunggu konfirmasi admin") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onVerifyClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Sudah Bayar")
                    }

                    Button(
                        onClick = onInvalidClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Tidak Valid")
                    }
                }
            }
        }
    }
}


fun fetchBookings(
    firestore: FirebaseFirestore,
    onResult: (List<BookingModel>) -> Unit
) {
    firestore.collection("users")
        .get()
        .addOnSuccessListener { usersSnapshot ->
            val allBookings = mutableListOf<BookingModel>()
            val tasks = mutableListOf<com.google.android.gms.tasks.Task<*>>()

            for (userDoc in usersSnapshot.documents) {
                val userId = userDoc.id
                val name = userDoc.getString("name") ?: "-"

                val task = firestore.collection("users")
                    .document(userId)
                    .collection("bookings")
                    .whereEqualTo("status", "menunggu konfirmasi admin")
                    .get()
                    .addOnSuccessListener { bookingSnap ->
                        for (doc in bookingSnap.documents) {
                            val booking = doc.toObject(BookingModel::class.java)
                            if (booking != null) {
                                allBookings.add(
                                    booking.copy(
                                        id = doc.id,
                                        userId = userId,
                                        name = name
                                    )
                                )
                            }
                        }
                    }
                tasks.add(task)
            }

            com.google.android.gms.tasks.Tasks.whenAllComplete(tasks)
                .addOnSuccessListener {
                    onResult(allBookings)
                }
        }
}

fun updateStatusToPaid(firestore: FirebaseFirestore, booking: BookingModel) {
    firestore.collection("users")
        .document(booking.userId)
        .collection("bookings")
        .document(booking.id)
        .update("status", "sudah bayar")
        .addOnSuccessListener {
            Log.d("Booking", "Status updated to 'sudah bayar'")
        }
        .addOnFailureListener {
            Log.e("Booking", "Gagal update status", it)
        }
}

fun updateStatusToInvalid(firestore: FirebaseFirestore, booking: BookingModel) {
    firestore.collection("users")
        .document(booking.userId)
        .collection("bookings")
        .document(booking.id)
        .update("status", "tidak valid")
        .addOnSuccessListener {
            Log.d("Booking", "Status updated to 'tidak valid'")
        }
        .addOnFailureListener {
            Log.e("Booking", "Gagal update status tidak valid", it)
        }
}
