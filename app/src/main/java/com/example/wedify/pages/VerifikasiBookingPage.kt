package com.example.wedify.pages

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wedify.model.BookingModel
import com.google.firebase.firestore.FirebaseFirestore


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifikasiBookingPage(navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    var bookings by remember { mutableStateOf<List<BookingModel>>(emptyList()) }

    LaunchedEffect(Unit) {
        fetchBookings(firestore) { result ->
            bookings = result
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Verifikasi Pembayaran") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            items(bookings) { booking ->
                BookingItem(booking) {
                    updateStatusToPaid(firestore, booking)
                    bookings = bookings.map {
                        if (it.id == booking.id) it.copy(status = "sudah bayar") else it
                    }
                }
            }
        }
    }
}

@Composable
fun BookingItem(booking: BookingModel, onVerifyClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Produk: ${booking.productName}")
            Text("Tanggal: ${booking.date} ${booking.time}")
            Text("Lokasi: ${booking.location}")
            Text("Total: Rp${booking.total}")
            Text("Status: ${booking.status}")
            Spacer(modifier = Modifier.height(8.dp))
            if (booking.status == "belum bayar") {
                Button(onClick = onVerifyClick) {
                    Text("Tandai Sudah Bayar")
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
                val task = firestore.collection("users")
                    .document(userId)
                    .collection("bookings")
                    .whereEqualTo("status", "belum bayar")
                    .get()
                    .addOnSuccessListener { bookingSnap ->
                        for (doc in bookingSnap.documents) {
                            val booking = doc.toObject(BookingModel::class.java)
                            if (booking != null) {
                                allBookings.add(
                                    booking.copy(id = doc.id, userId = userId)
                                )
                            }
                        }
                    }
                tasks.add(task)
            }

            // Wait until all tasks finished
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
