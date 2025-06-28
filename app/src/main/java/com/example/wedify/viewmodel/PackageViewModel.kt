package com.example.wedify.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wedify.model.ProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {
    private val _products = MutableStateFlow<List<ProductModel>>(emptyList())
    val products: StateFlow<List<ProductModel>> = _products

    init {
        fetchProductsByVendor()
    }
    private fun fetchProductsByVendor() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            FirebaseFirestore.getInstance()
                .collection("data")
                .document("stok")
                .collection("products")
                .whereEqualTo("vendorId", uid) // ✅ ini yang benar
                .addSnapshotListener { snapshot, _ ->
                    val list = snapshot?.documents?.mapNotNull {
                        val product = it.toObject(ProductModel::class.java)
                        product?.copy(id = it.id) // set id dari dokumen Firestore
                    }
                    _products.value = list ?: emptyList()
                }
        }
    }
    fun deleteProduct(productId: String) {
        FirebaseFirestore.getInstance()
            .collection("data")
            .document("stok")
            .collection("products")
            .document(productId)
            .delete()
    }
}
