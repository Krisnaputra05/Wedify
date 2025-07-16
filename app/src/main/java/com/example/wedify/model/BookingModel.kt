package com.example.wedify.model

data class BookingModel(
    val id: String = "",
    val userId: String = "",
    val productId: String = "",
    val productName: String = "",
    val category: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "",
    val total: Long = 0L,
    val status: String = "",
    val buktiTransferUrl: String? = null,
    val name: String? = null
)
