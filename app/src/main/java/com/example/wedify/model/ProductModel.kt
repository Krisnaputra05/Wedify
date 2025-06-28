package com.example.wedify.model

data class ProductModel(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var price: String = "",
    var actualPrice: String = "",
    var category: String = "",
    var location: String = "",
    var images: List<String> = listOf(),
    var otherDetails: Map<String, String> = mapOf(),
    var vendorId: String = "" // Tambahkan field ini!
)

