package com.example.wedify

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


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

    /**
     * Cek apakah ID produk valid (alfanumerik, panjang ≥ 4)
     */
    private fun isValidProductId(productId: String): Boolean {
        val regex = Regex("^[a-zA-Z0-9_-]{4,}$")
        return regex.matches(productId.trim())
    }
}
