package com.example.wedify

import android.content.Context
import android.widget.Toast
import com.example.wedify.model.BookingModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.app.DatePickerDialog
import com.example.wedify.model.ProductModel
import java.util.*

object AppUtil {

    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()


    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun addToCart(context: Context, productId: String) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            showToast(context, "Anda belum login.")
            return
        }

        if (!isValidProductId(productId)) {
            showToast(context, "ID produk tidak valid.")
            return
        }

        val userRef = db.collection("users").document(uid)

        userRef.get().addOnSuccessListener { doc ->
            val currentCart = doc.get("cartItems") as? Map<String, Long> ?: emptyMap()
            val currentQty = currentCart[productId] ?: 0L
            val updatedQty = currentQty + 1

            val updateMap = mapOf("cartItems.$productId" to updatedQty)

            userRef.update(updateMap)
                .addOnSuccessListener {
                    showToast(context, "Produk berhasil ditambahkan.")
                }
                .addOnFailureListener {
                    showToast(context, "Gagal menambahkan produk: ${it.message}")
                }
        }.addOnFailureListener {
            showToast(context, "Gagal memuat data pengguna.")
        }
    }

    fun deleteToCart(context: Context, productId: String) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            showToast(context, "Anda belum login.")
            return
        }

        if (!isValidProductId(productId)) {
            showToast(context, "ID produk tidak valid.")
            return
        }

        val userRef = db.collection("users").document(uid)

        userRef.get().addOnSuccessListener { doc ->
            val currentCart = doc.get("cartItems") as? Map<String, Long> ?: emptyMap()
            val currentQty = currentCart[productId] ?: 0L
            val updatedQty = currentQty - 1;


            val updateMap =
                if (updatedQty <= 0)
                    mapOf("cartItems.$productId" to FieldValue.delete())
                else
                    mapOf("cartItems.$productId" to updatedQty)

            userRef.update(updateMap)
                .addOnSuccessListener {
                    showToast(context, "Produk berhasil ditambahkan.")
                }
                .addOnFailureListener {
                    showToast(context, "Gagal menambahkan produk: ${it.message}")
                }
        }.addOnFailureListener {
            showToast(context, "Gagal memuat data pengguna.")
        }
    }

    fun removeFromCart(context: Context, productId: String) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            showToast(context, "Anda belum login.")
            return
        }
        val userRef = db.collection("users").document(uid)

        val updateMap = mapOf<String, Any>("cartItems.$productId" to FieldValue.delete())

        userRef.update(updateMap)
            .addOnSuccessListener {
                showToast(context, "Produk dihapus dari keranjang.")
            }
            .addOnFailureListener {
                showToast(context, "Gagal menghapus produk: ${it.message}")
            }
    }

    fun getDiscountPercentage(): Float {
        return 10.0f
    }

    fun getTaxPercentage(): Float {
        return 4.0f
    }

    fun saveBookingToUser(context: Context, booking: BookingModel) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            showToast(context, "Anda belum login.")
            return
        }

        val userRef = db.collection("users").document(uid)

        // Konversi BookingModel ke Map
        val bookingMap = mapOf(
            "lokasi" to booking.location,
            "tanggal" to booking.date,
            "jam" to booking.time,
            "timestamp" to FieldValue.serverTimestamp() // supaya urut by waktu booking
        )

        // Simpan ke collection "bookings" di bawah user
        userRef.collection("bookings").add(bookingMap)
            .addOnSuccessListener {
                showToast(context, "Booking berhasil disimpan.")
            }
            .addOnFailureListener {
                showToast(context, "Gagal menyimpan booking: ${it.message}")
            }
    }
    fun addBookingDirect(context: Context, productId: String, onSuccess: (String) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            showToast(context, "Anda belum login.")
            return
        }

        if (!isValidProductId(productId)) {
            showToast(context, "ID produk tidak valid.")
            return
        }

        val productRef = Firebase.firestore
            .collection("data")
            .document("stok")
            .collection("products")
            .document(productId)

        productRef.get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                val product = doc.toObject(com.example.wedify.model.ProductModel::class.java)
                if (product != null) {
                    val bookingData = hashMapOf(
                        "date" to "-",
                        "time" to "-",
                        "location" to "-",
                        "total" to product.actualPrice.toFloat().toLong(),
                        "status" to "belum bayar",
                        "productId" to product.id,
                        "productName" to product.title,
                        "category" to product.category
                    )

                    Firebase.firestore.collection("users")
                        .document(uid)
                        .collection("bookings")
                        .add(bookingData)
                        .addOnSuccessListener { documentRef ->
                            val bookingId = documentRef.id
                            showToast(context, "Booking berhasil dibuat.")
                            onSuccess(bookingId)
                        }
                        .addOnFailureListener {
                            showToast(context, "Gagal menyimpan booking: ${it.message}")
                        }
                } else {
                    showToast(context, "Data produk tidak ditemukan.")
                }
            } else {
                showToast(context, "Produk tidak ditemukan di database.")
            }
        }.addOnFailureListener {
            showToast(context, "Gagal mengambil data produk: ${it.message}")
        }
    }



    fun savePaymentToBooking(context: Context, bookingId: String, paymentStatus: String) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            showToast(context, "Anda belum login.")
            return
        }

        val bookingRef = db.collection("users")
            .document(uid)
            .collection("bookings")
            .document(bookingId)

        val paymentMap = mapOf(
            "paymentStatus" to paymentStatus,
            "paymentTimestamp" to FieldValue.serverTimestamp()
        )

        bookingRef.set(paymentMap, SetOptions.merge())
            .addOnSuccessListener {
                showToast(context, "Pembayaran berhasil disimpan.")
            }
            .addOnFailureListener {
                showToast(context, "Gagal menyimpan pembayaran: ${it.message}")
            }
    }


    fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(context, { _, y, m, d ->
            val selectedDate = String.format("%02d/%02d/%d", d, m + 1, y)
            onDateSelected(selectedDate)
        }, year, month, day)

        datePicker.show()
    }

    fun showTimePicker(context: Context, onTimeSelected: (String) -> Unit) {
        val calendar = java.util.Calendar.getInstance()
        val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = calendar.get(java.util.Calendar.MINUTE)

        val timePickerDialog = android.app.TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                val timeString = String.format("%02d:%02d", selectedHour, selectedMinute)
                onTimeSelected(timeString)
            },
            hour, minute, true
        )

        timePickerDialog.show()
    }


    /**
     * Cek apakah ID produk valid (alfanumerik, panjang ≥ 4)
     */
    private fun isValidProductId(productId: String): Boolean {
        val regex = Regex("^[a-zA-Z0-9_-]{4,}$")
        return regex.matches(productId.trim())
    }
}
